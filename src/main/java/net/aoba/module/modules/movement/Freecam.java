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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Freecam extends Module implements TickListener, Render3DListener {
	private final FloatSetting flySpeed = FloatSetting.builder().id("freecam_speed").displayName("Speed")
			.description("Speed of the Freecam.").defaultValue(2f).minValue(0.1f).maxValue(15f).step(0.5f).build();

	private FakePlayerEntity fakePlayer;
	private Vec3d prevPos;
	private Vec3d pos;

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

		ClientPlayerEntity player = MC.player;
		fakePlayer = new FakePlayerEntity();
		fakePlayer.copyFrom(player);
		fakePlayer.setUuid(UUID.randomUUID());
		fakePlayer.headYaw = player.headYaw;
		fakePlayer.bodyYaw = player.bodyYaw;
		fakePlayer.setPitch(player.getPitch());
		MC.world.addEntity(fakePlayer);

		Camera camera = MC.gameRenderer.getCamera();
		ICamera iCamera = (ICamera) camera;
		iCamera.setFocusedEntity(null);

		Vec3d newPos = MC.player.getPos().add(0, 1.5, 0);
		prevPos = newPos;
		pos = newPos;
		iCamera.setCameraPos(pos);

	}

	@Override
	public void onToggle() {
	}

	@Override
	public void onRender(Render3DEvent event) {
		Camera camera = MC.gameRenderer.getCamera();
		ICamera iCamera = (ICamera) camera;

		float tickDelta = event.getRenderTickCounter().getTickProgress(true);

		ClientPlayerEntity player = MC.player;
		fakePlayer.setPitch(player.getPitch(tickDelta));

		Vec3d interpolatedPos = new Vec3d(MathHelper.lerp(tickDelta, prevPos.x, pos.x),
				MathHelper.lerp(tickDelta, prevPos.y, pos.y), MathHelper.lerp(tickDelta, prevPos.z, pos.z));
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
		Camera camera = MC.gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();
		prevPos = cameraPos;

		Vec3d forward = Vec3d.fromPolar(0, camera.getYaw());
		Vec3d right = Vec3d.fromPolar(0, camera.getYaw() + 90);

		Vec3d velocity = new Vec3d(0, 0, 0);

		if (MC.options.forwardKey.isPressed()) {
			velocity = velocity.add(forward.multiply(flySpeed.getValue()));
		} else if (MC.options.backKey.isPressed()) {
			velocity = velocity.subtract(forward.multiply(flySpeed.getValue()));
		}

		if (MC.options.rightKey.isPressed()) {
			velocity = velocity.add(right.multiply(flySpeed.getValue()));
		} else if (MC.options.leftKey.isPressed()) {
			velocity = velocity.subtract(right.multiply(flySpeed.getValue()));
		}

		if (MC.options.jumpKey.isPressed()) {
			velocity = velocity.add(0, flySpeed.getValue(), 0);
		} else if (MC.options.sneakKey.isPressed())
			velocity = velocity.add(0, -flySpeed.getValue(), 0);

		pos = cameraPos.add(velocity);

		ClientPlayerEntity player = MC.player;
		fakePlayer.setHeadYaw(player.getHeadYaw());
		fakePlayer.setBodyYaw(player.getBodyYaw());
		fakePlayer.setVelocity(player.getVelocity());
		fakePlayer.setPosition(player.getPos());
		fakePlayer.setUuid(player.getUuid());
	}
}
