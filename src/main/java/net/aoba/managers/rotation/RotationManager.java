/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.rotation;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.SendMovementPacketEvent;
import net.aoba.event.events.SendPacketEvent;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.SendMovementPacketListener;
import net.aoba.event.listeners.SendPacketListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.managers.rotation.goals.Goal;
import net.aoba.mixin.interfaces.ILocalPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;

public class RotationManager implements TickListener, Render3DListener, SendPacketListener, SendMovementPacketListener {
	private static final Minecraft MC = Minecraft.getInstance();
	private static final AobaClient AOBA = Aoba.getInstance();

	private Goal<?> currentGoal = null;

	private Float lastServerYaw = null;
	private Float lastServerPitch = null;
	private Float serverYaw = null;
	private Float serverPitch = null;

	public RotationManager() {
		AOBA.eventManager.AddListener(TickListener.class, this);
		AOBA.eventManager.AddListener(Render3DListener.class, this);
		AOBA.eventManager.AddListener(SendPacketListener.class, this);
		AOBA.eventManager.AddListener(SendMovementPacketListener.class, this);
	}

	public static double getGCD() {
		double f = MC.options.sensitivity().get() * 0.6 + 0.2;
		return f * f * f * 1.2;
	}

	public Goal<?> getGoal() {
		return currentGoal;
	}

	public void setGoal(Goal<?> goal) {
		currentGoal = goal;
	}

	@Override
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {

	}

	@Override
	public void onRender(Render3DEvent event) {
		if (MC.player == null)
			return;

		if (serverPitch == null)
			serverPitch = MC.player.getXRot();

		if (serverYaw == null)
			serverYaw = MC.player.getYRot();

		lastServerYaw = serverYaw;
		lastServerPitch = serverPitch;

		if (currentGoal != null) {
			float tickDelta = event.getRenderTickCounter().getGameTimeDeltaPartialTick(true);
			Rotation currentGoalRotation;
			if (currentGoal.isFakeRotation()) {
				currentGoalRotation = getRotationFromGoal(serverYaw, serverPitch, tickDelta);
				if (currentGoalRotation != null) {
					serverYaw = (float) currentGoalRotation.yaw();
					serverPitch = (float) currentGoalRotation.pitch();
				}
			} else {
				currentGoalRotation = getRotationFromGoal(MC.player.getYRot(), MC.player.getXRot(), tickDelta);
				if (currentGoalRotation != null) {
					MC.player.setYRot((float) currentGoalRotation.yaw());
					MC.player.setXRot((float) currentGoalRotation.pitch());
				}
			}
		}
	}

	@Override
	public void onSendPacket(SendPacketEvent event) {

	}

	private Rotation getRotationFromGoal(float startYaw, float startPitch, float tickDelta) {
		Rotation currentGoalRotation = currentGoal.getGoalRotation(tickDelta);

		switch (currentGoal.getRotationMode()) {
		case NONE:
			return null;
		case SMOOTH:
			// Gets the difference between the players view and the goal.
			Rotation difference = Rotation.difference(new Rotation(startYaw, startPitch), currentGoalRotation);

			// Calculate the max distance allowable to rotate during this frame.
			float rotationDegreesPerTick = currentGoal.getMaxRotation() * tickDelta;
			float maxYawRotationDelta = Math.clamp((float) -difference.yaw(), -rotationDegreesPerTick,
					rotationDegreesPerTick);
			float maxPitchRotation = Math.clamp((float) -difference.pitch(), -rotationDegreesPerTick,
					rotationDegreesPerTick);

			// Apply Pitch / Yaw randomness
			double pitchRandom = Math.random() * currentGoal.getPitchRandomness();
			double yawRandom = Math.random() * currentGoal.getYawRandomness();

			maxYawRotationDelta += yawRandom;
			maxPitchRotation += pitchRandom;

			// Create new rotation and set player yaw and pitch to the rotation.
			Rotation newRotation = new Rotation(startYaw + maxYawRotationDelta, startPitch + maxPitchRotation)
					.roundToGCD().clamp();
			return newRotation;
		case INSTANT:
			return currentGoalRotation;
		default:
			return null;
		}
	}

	@Override
	public void onSendMovementPacket(SendMovementPacketEvent.Pre event) {
		if (currentGoal == null || !currentGoal.isFakeRotation())
			return;

		if (currentGoal.getRotationMode() == RotationMode.NONE)
			return;

		// Fabricate our own packet.
		ILocalPlayer iPlayer = (ILocalPlayer) MC.player;

		double d = MC.player.getX() - MC.player.xo;
		double e = MC.player.getY() - MC.player.yo;
		double f = MC.player.getZ() - MC.player.zo;
		double g = serverYaw - lastServerYaw;
		double h = serverPitch - lastServerPitch;

		boolean bl = Mth.lengthSquared(d, e, f) > Mth.square(2.0E-4);
		boolean bl2 = g != 0.0 || h != 0.0;

		if (bl && bl2) {
			event.cancel();
			MC.getConnection().send(new ServerboundMovePlayerPacket.PosRot(MC.player.getX(), MC.player.getY(),
					MC.player.getZ(), serverYaw, serverPitch, MC.player.onGround(), MC.player.horizontalCollision));
		} else if (bl2) {
			event.cancel();
			MC.getConnection().send(new ServerboundMovePlayerPacket.Rot(serverYaw, serverPitch,
					MC.player.onGround(), MC.player.horizontalCollision));
		} else
			return; // View was not affected, return.

		if (bl) {
			MC.player.xo = MC.player.getX();
			MC.player.yo = MC.player.getY();
			MC.player.zo = MC.player.getZ();
			iPlayer.setTicksSinceLastPositionPacketSent(0);
		}

		iPlayer.setLastOnGround(MC.player.onGround());
		iPlayer.setLastHorizontalCollision(MC.player.horizontalCollision);
		iPlayer.setAutoJumpEnabled(MC.options.autoJump().get());
	}

	@Override
	public void onSendMovementPacket(SendMovementPacketEvent.Post event) {

	}
}
