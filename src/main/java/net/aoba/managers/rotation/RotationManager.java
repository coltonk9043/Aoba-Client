/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.rotation;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.managers.rotation.goals.Goal;
import net.minecraft.client.MinecraftClient;

public class RotationManager implements TickListener, Render3DListener {
	private static MinecraftClient MC = MinecraftClient.getInstance();

	private Goal<?> currentGoal = null;

	public RotationManager() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
	}

	public static double getGCD() {
		double f = MC.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
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

		if (currentGoal != null) {
			Rotation currentGoalRotation = currentGoal.getGoalRotation(event.getRenderTickCounter().getTickDelta(true));

			switch (currentGoal.getRotationMode()) {
			case RotationMode.NONE:
				// tbh idek why you would want this, but i'll keep it in for KillAura n shit.
				break;
			case RotationMode.SMOOTH:
				// Gets the difference between the players view and the goal.
				Rotation difference = Rotation.difference(new Rotation(MC.player.getYaw(), MC.player.getPitch()),
						currentGoalRotation);

				// Calculate the max distance allowable to rotate during this frame.
				float rotationDegreesPerTick = currentGoal.getMaxRotation()
						* event.getRenderTickCounter().getTickDelta(true);
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
				Rotation newRotation = new Rotation(MC.player.getYaw() + maxYawRotationDelta,
						MC.player.getPitch() + maxPitchRotation).roundToGCD();
				MC.player.setYaw((float) newRotation.yaw());
				MC.player.setPitch((float) newRotation.pitch());
				break;
			case RotationMode.INSTANT:
				// Instantly moves the players head to the target.
				MC.player.setYaw((float) currentGoalRotation.yaw());
				MC.player.setPitch((float) currentGoalRotation.pitch());
				break;
			}
		}
	}
}