/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import java.util.UUID;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.mixin.interfaces.ICamera;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.entity.FakePlayerEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Freecam extends Module implements TickListener, Render3DListener {
	private final FloatSetting flySpeed = FloatSetting.builder().id("freecam_speed").displayName("Speed")
			.description("Speed of the Freecam.").defaultValue(2f).minValue(0.1f).maxValue(15f).step(0.5f).build();

	private FakePlayerEntity fakePlayer;
	private Vec3 prevPos;
	private Vec3 pos;

	public Freecam() {
		super("Freecam");
		setCategory(Category.of("Movement"));
		setDescription("Allows the player to clip through blocks (Only work clientside).");
		addSetting(flySpeed);
	}

	public void setSpeed(float speed) {
		flySpeed.setValue(speed);
	}

	public double getSpeed() {
		return flySpeed.getValue();
	}

	@Override
	public void onDisable() {
		if (fakePlayer != null)
			fakePlayer.despawn();

		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);

		LocalPlayer player = MC.player;
		fakePlayer = new FakePlayerEntity();
		fakePlayer.restoreFrom(player);
		fakePlayer.setUUID(UUID.randomUUID());
		fakePlayer.yHeadRot = player.yHeadRot;
		fakePlayer.yBodyRot = player.yBodyRot;
		fakePlayer.setXRot(player.getXRot());
		MC.level.addEntity(fakePlayer);

		Camera camera = MC.gameRenderer.getMainCamera();
		ICamera iCamera = (ICamera) camera;

		Vec3 newPos = MC.player.position().add(0, 1.5, 0);
		prevPos = newPos;
		pos = newPos;
		iCamera.setCameraPos(pos);

	}

	@Override
	public void onToggle() {
	}

	@Override
	public void onRender(Render3DEvent event) {
		if (fakePlayer == null || MC.player == null)
			return;

		Camera camera = MC.gameRenderer.getMainCamera();
		ICamera iCamera = (ICamera) camera;

		float tickDelta = event.getRenderer().getDeltaTracker().getGameTimeDeltaPartialTick(true);

		LocalPlayer player = MC.player;
		fakePlayer.setXRot(player.getViewXRot(tickDelta));

		Vec3 interpolatedPos = new Vec3(Mth.lerp(tickDelta, prevPos.x, pos.x),
				Mth.lerp(tickDelta, prevPos.y, pos.y), Mth.lerp(tickDelta, prevPos.z, pos.z));
		iCamera.setCameraPos(interpolatedPos);
	}

	public FakePlayerEntity getFakePlayer() {
		return fakePlayer;
	}

	@Override
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		if (fakePlayer == null || MC.player == null)
			return;

		Camera camera = MC.gameRenderer.getMainCamera();
		Vec3 cameraPos = camera.position();
		prevPos = cameraPos;

		Vec3 forward = Vec3.directionFromRotation(0, camera.yRot());
		Vec3 right = Vec3.directionFromRotation(0, camera.yRot() + 90);

		Vec3 velocity = new Vec3(0, 0, 0);

		if (MC.options.keyUp.isDown()) {
			velocity = velocity.add(forward.scale(flySpeed.getValue()));
		} else if (MC.options.keyDown.isDown()) {
			velocity = velocity.subtract(forward.scale(flySpeed.getValue()));
		}

		if (MC.options.keyRight.isDown()) {
			velocity = velocity.add(right.scale(flySpeed.getValue()));
		} else if (MC.options.keyLeft.isDown()) {
			velocity = velocity.subtract(right.scale(flySpeed.getValue()));
		}

		if (MC.options.keyJump.isDown()) {
			velocity = velocity.add(0, flySpeed.getValue(), 0);
		} else if (MC.options.keyShift.isDown())
			velocity = velocity.add(0, -flySpeed.getValue(), 0);

		pos = cameraPos.add(velocity);

		LocalPlayer player = MC.player;
		fakePlayer.setYHeadRot(player.getYHeadRot());
		fakePlayer.setYBodyRot(player.getVisualRotationYInDegrees());
		fakePlayer.setDeltaMovement(player.getDeltaMovement());
		fakePlayer.setPos(player.position());
		fakePlayer.setUUID(player.getUUID());
	}
}
