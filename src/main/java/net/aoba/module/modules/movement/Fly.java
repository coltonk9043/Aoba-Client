/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

enum FlyMode {
	Synthetic, Natural
}

public class Fly extends Module implements TickListener {

	private final EnumSetting<FlyMode> flyMode = EnumSetting.<FlyMode>builder().id("fly_mode").displayName("Mode")
			.description("The method used to simulate flying.").defaultValue(FlyMode.Synthetic).build();

	private final FloatSetting flySpeed = FloatSetting.builder().id("fly_speed").displayName("Speed")
			.description("Fly speed.").defaultValue(2f).minValue(0.05f).maxValue(15f).step(0.05f).build();

	private final FloatSetting sprintSpeedMultiplier = FloatSetting.builder().id("fly_sprint_speed_multiplier")
			.displayName("Sprint Speed Multiplier").description("Speed multiplier when sprinting.").defaultValue(1.5f)
			.minValue(1.0f).maxValue(3.0f).step(0.1f).build();

	private final FloatSetting jumpMotionY = FloatSetting.builder().id("fly_jump_motion_y").displayName("Ascend Speed")
			.description("Upward motion when jump key is pressed.").defaultValue(0.3f).minValue(0.1f).maxValue(2.0f)
			.step(0.1f).build();

	private final FloatSetting sneakMotionY = FloatSetting.builder().id("fly_sneak_motion_y")
			.displayName("Descend Speed").description("Downward motion when sneak key is pressed.").defaultValue(-0.3f)
			.minValue(-2.0f).maxValue(0f).step(0.1f).build();

	private final FloatSetting acceleration = FloatSetting.builder().id("fly_acceleration").displayName("Acceleration")
			.description("The acceleration of the fly speed.").defaultValue(0.3f).minValue(0.2f).maxValue(1f)
			.step(0.01f).build();

	private final FloatSetting momentum = FloatSetting.builder().id("fly_momentum").displayName("Momentum")
			.description("The percentage of speed maintained after a button is released..").defaultValue(0.3f)
			.minValue(0f).maxValue(1f).step(0.01f).build();

	private final BooleanSetting antiKick = BooleanSetting.builder().id("fly_antikick").displayName("AntiKick")
			.description("Prevents the player from being kicked.").defaultValue(false).build();

	private Vec3d previousVelocity = new Vec3d(0, 0, 0);
	private Vec3d accelerationVector = new Vec3d(0, 0, 0); // X - horizontal, Y = vertical, Z = depth

	public Fly() {
		super("Fly");
		setCategory(Category.of("Movement"));
		setDescription("Allows the player to fly.");

		addSetting(flyMode);
		addSetting(flySpeed);
		addSetting(sprintSpeedMultiplier);
		addSetting(jumpMotionY);
		addSetting(sneakMotionY);
		addSetting(acceleration);
		addSetting(momentum);
		addSetting(antiKick);

		setDetectable(AntiCheat.NoCheatPlus, AntiCheat.Vulcan, AntiCheat.AdvancedAntiCheat, AntiCheat.Verus,
				AntiCheat.Grim, AntiCheat.Matrix, AntiCheat.Negativity, AntiCheat.Karhu);
	}

	public void setSpeed(float speed) {
		flySpeed.setValue(speed);
	}

	public double getSpeed() {
		return flySpeed.getValue();
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
		ClientPlayerEntity player = MC.player;
		float speed = flySpeed.getValue().floatValue();
		float speedSqr = flySpeed.getValueSqr().floatValue();

		switch (flyMode.getValue()) {
		case FlyMode.Synthetic:
			// Check if the player is riding.
			if (MC.player.isRiding()) {
				Entity riding = MC.player.getRootVehicle();
				Vec3d velocity = riding.getVelocity();
				double motionY = MC.options.jumpKey.isPressed() ? jumpMotionY.getValue() : 0;
				riding.setVelocity(velocity.x, motionY, velocity.z);
			} else {
				float sprintMultiplier = sprintSpeedMultiplier.getValue().floatValue();
				float momentumValue = momentum.getValue();
				float accelerationValue = acceleration.getValueSqr();

				if (MC.options.sprintKey.isPressed()) {
					speed *= sprintMultiplier;
				}

				// Reset player abilities
				player.getAbilities().flying = false;
				MC.player.setVelocity(0, 0, 0);

				// Calculate vectors
				double yawRad = Math.toRadians(MC.cameraEntity.getYaw());
				Vec3d forward = new Vec3d(-Math.sin(yawRad), 0, Math.cos(yawRad)).multiply(speed);
				Vec3d right = new Vec3d(-Math.cos(yawRad), 0, -Math.sin(yawRad)).multiply(speed);
				Vec3d momentumVec = previousVelocity.multiply(1.0 - momentumValue, 0, 1.0 - momentumValue);

				// Determine how much acceleration to apply based on inputs. (XOR)
				if (!(MC.options.forwardKey.isPressed() ^ MC.options.backKey.isPressed())) {
					accelerationVector = new Vec3d(accelerationVector.x, accelerationVector.y, 0);
				} else if (MC.options.forwardKey.isPressed()) {
					accelerationVector = new Vec3d(accelerationVector.x, accelerationVector.y,
							Math.max(0, accelerationVector.z + accelerationValue));
				} else if (MC.options.backKey.isPressed()) {
					accelerationVector = new Vec3d(accelerationVector.x, accelerationVector.y,
							Math.min(0, accelerationVector.z - accelerationValue));
				}

				if (!(MC.options.rightKey.isPressed() ^ MC.options.leftKey.isPressed())) {
					accelerationVector = new Vec3d(0, accelerationVector.y, accelerationVector.z);
				} else if (MC.options.rightKey.isPressed()) {
					accelerationVector = new Vec3d(Math.max(0, accelerationVector.x + accelerationValue),
							accelerationVector.y, accelerationVector.z);
				} else if (MC.options.leftKey.isPressed()) {
					accelerationVector = new Vec3d(Math.min(0, accelerationVector.x - accelerationValue),
							accelerationVector.y, accelerationVector.z);
				}

				// Apply velocity based on acceleration
				Vec3d vec = new Vec3d(previousVelocity.x, 0, previousVelocity.z).subtract(momentumVec)
						.add(forward.multiply(accelerationVector.z)).add(right.multiply(accelerationVector.x));

				// Apply vertical motion (does not use acceleration or momentum values)
				if (MC.options.jumpKey.isPressed()) {
					vec = vec.add(0, jumpMotionY.getValue(), 0);
				}

				if (MC.options.sneakKey.isPressed()) {
					vec = vec.add(0, sneakMotionY.getValue(), 0);
				}

				// Apply antikick.
				if (antiKick.getValue()) {
					vec = vec.add(0, -0.08, 0);
				}

				// We accelerated faster than our limit. Clamp it.
				if (vec.lengthSquared() > speedSqr) {
					vec = vec.normalize().multiply(speed);
				}

				previousVelocity = vec;
				player.setVelocity(vec);
			}
			break;
		case FlyMode.Natural:
			// TODO: We move hella fast like this... why?
			if (!player.isSpectator()) {
				player.getAbilities().setFlySpeed(speed);
				player.getAbilities().flying = true;

				if (!player.getAbilities().creativeMode) {
					player.getAbilities().allowFlying = true;
				}
			}
			break;
		}
	}
}