/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.SendMovementPacketEvent.Post;
import net.aoba.event.events.SendMovementPacketEvent.Pre;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.SendMovementPacketListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Fly extends Module implements TickListener, SendMovementPacketListener {

	private final FloatSetting flySpeed = FloatSetting.builder().id("fly_speed").displayName("Speed")
			.description("Fly speed.").defaultValue(2f).minValue(0.1f).maxValue(15f).step(0.5f).build();

	private final FloatSetting sprintSpeedMultiplier = FloatSetting.builder().id("fly_sprint_speed_multiplier")
			.displayName("Sprint Speed Multiplier").description("Speed multiplier when sprinting.").defaultValue(1.5f)
			.minValue(1.0f).maxValue(3.0f).step(0.1f).build();

	private final FloatSetting jumpMotionY = FloatSetting.builder().id("fly_jump_motion_y").displayName("Jump Motion Y")
			.description("Upward motion when jump key is pressed.").defaultValue(0.3f).minValue(0.1f).maxValue(2.0f)
			.step(0.1f).build();

	private final FloatSetting sneakMotionY = FloatSetting.builder().id("fly_sneak_motion_y")
			.displayName("Sneak Motion Y").description("Downward motion when sneak key is pressed.").defaultValue(-0.3f)
			.minValue(-2.0f).maxValue(0f).step(0.1f).build();

	private final BooleanSetting antiKick = BooleanSetting.builder().id("fly_antikick").displayName("AntiKick")
			.description("Prevents the player from being kicked.").defaultValue(false).build();

	public Fly() {
		super("Fly");
		this.setCategory(Category.of("Movement"));
		this.setDescription("Allows the player to fly.");

		this.addSetting(flySpeed);
		this.addSetting(sprintSpeedMultiplier);
		this.addSetting(jumpMotionY);
		this.addSetting(sneakMotionY);
		this.addSetting(antiKick);

		this.setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Verus,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity,
		    AntiCheat.Karhu
		);
	}

	public void setSpeed(float speed) {
		this.flySpeed.setValue(speed);
	}

	public double getSpeed() {
		return this.flySpeed.getValue();
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(SendMovementPacketListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(SendMovementPacketListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSendMovementPacket(Pre event) {
		ClientPlayerEntity player = MC.player;
		float speed = this.flySpeed.getValue().floatValue();
		if (MC.player.isRiding()) {
			Entity riding = MC.player.getRootVehicle();
			Vec3d velocity = riding.getVelocity();
			double motionY = MC.options.jumpKey.isPressed() ? jumpMotionY.getValue() : 0;
			riding.setVelocity(velocity.x, motionY, velocity.z);
		} else {
			float sprintMultiplier = this.sprintSpeedMultiplier.getValue().floatValue();
			if (MC.options.sprintKey.isPressed()) {
				speed *= sprintMultiplier;
			}
			player.getAbilities().flying = false;
			player.setVelocity(new Vec3d(0, 0, 0));

			double yawRad = Math.toRadians(MC.cameraEntity.getYaw());

			Vec3d forward = new Vec3d(-Math.sin(yawRad), 0, Math.cos(yawRad));
			Vec3d right = new Vec3d(-Math.cos(yawRad), 0, -Math.sin(yawRad));

			Vec3d vec = new Vec3d(0, 0, 0);
			if (MC.options.forwardKey.isPressed())
				vec = vec.add(forward.multiply(speed));
			if (MC.options.backKey.isPressed())
				vec = vec.add(forward.multiply(speed).multiply(-1));

			if (MC.options.rightKey.isPressed())
				vec = vec.add(right.multiply(speed));
			if (MC.options.leftKey.isPressed())
				vec = vec.add(right.multiply(speed).multiply(-1));

			if (MC.options.jumpKey.isPressed())
				vec = vec.add(0, jumpMotionY.getValue(), 0);
			if (MC.options.sneakKey.isPressed())
				vec = vec.add(0, sneakMotionY.getValue(), 0);

			player.setVelocity(vec);
		}

		if (antiKick.getValue())
			MC.player.setVelocity(MC.player.getVelocity().add(0, -0.08, 0));
	}

	@Override
	public void onSendMovementPacket(Post event) {

	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {

	}
}