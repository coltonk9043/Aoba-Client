/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class Jetpack extends Module implements TickListener {

	private final FloatSetting jetpackSpeed = FloatSetting.builder().id("jetpack_speed").displayName("Speed")
			.description("Jetpack Speed.").defaultValue(0.5f).minValue(0.1f).maxValue(5f).step(0.1f).build();

	private final FloatSetting thrusterParticleAmount = FloatSetting.builder().id("jetpack_particle_amount")
			.displayName("Thruster Particle Amount").description("Number of particles generated by thrusters.")
			.defaultValue(1f).minValue(1f).maxValue(20f).step(1f).build();

	private final FloatSetting jumpMotionY = FloatSetting.builder().id("jetpack_jump_motion_y")
			.displayName("Jump Motion Y").description("Upward motion when jump key is pressed.").defaultValue(0.3f)
			.minValue(0.1f).maxValue(2f).step(0.1f).build();

	private final FloatSetting thrusterSpread = FloatSetting.builder().id("jetpack_thruster_spread")
			.displayName("Thruster Spread").description("Spread of the thruster particles.").defaultValue(0.25f)
			.minValue(0.1f).maxValue(1f).step(0.1f).build();

	public Jetpack() {
		super("Jetpack");
		this.setCategory(Category.of("Movement"));
		this.setDescription("Like fly, but a lot more fun!");

		this.addSetting(jetpackSpeed);
		this.addSetting(thrusterParticleAmount);
		this.addSetting(jumpMotionY);
		this.addSetting(thrusterSpread);

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
		this.jetpackSpeed.setValue(speed);
	}

	public double getSpeed() {
		return this.jetpackSpeed.getValue();
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
	public void onTick(Pre event) {
		ClientPlayerEntity player = MC.player;
		float speed = this.jetpackSpeed.getValue().floatValue();

		if (MC.player.fallDistance > 2f) {
			MC.player.networkHandler.sendPacket(new OnGroundOnly(true, false));
		}

		if (MC.player.isRiding()) {
			Entity riding = MC.player.getRootVehicle();
			Vec3d velocity = riding.getVelocity();
			double motionY = MC.options.jumpKey.isPressed() ? jumpMotionY.getValue() : 0;
			riding.setVelocity(velocity.x, motionY, velocity.z);
		} else {
			player.getAbilities().flying = false;

			Vec3d playerSpeed = player.getVelocity();
			if (MC.options.jumpKey.isPressed()) {
				double angle = -player.bodyYaw;
				float spread = thrusterSpread.getValue();
				float leftThrusterX = (float) Math.sin(Math.toRadians(angle + 90)) * spread;
				float leftThrusterZ = (float) Math.cos(Math.toRadians(angle + 90)) * spread;
				float rightThrusterX = (float) Math.sin(Math.toRadians(angle + 270)) * spread;
				float rightThrusterZ = (float) Math.cos(Math.toRadians(angle + 270)) * spread;

				int particleAmount = thrusterParticleAmount.getValue().intValue();
				for (int i = 0; i < particleAmount; i++) {
					MC.world.addParticle(ParticleTypes.FLAME, player.getX() + leftThrusterX, player.getY() + 0.5f,
							player.getZ() + leftThrusterZ, leftThrusterX, -0.5f, leftThrusterZ);
					MC.world.addParticle(ParticleTypes.FLAME, player.getX() + rightThrusterX, player.getY() + 0.5f,
							player.getZ() + rightThrusterZ, rightThrusterX, -0.5f, rightThrusterZ);
				}
				playerSpeed = playerSpeed.add(0, speed / 20.0f, 0);
			}
			player.setVelocity(playerSpeed);
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
