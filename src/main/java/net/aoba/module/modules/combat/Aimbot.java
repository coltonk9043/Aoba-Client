/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.EntityGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class Aimbot extends Module implements TickListener {

	private LivingEntity temp = null;

	private final BooleanSetting targetAnimals = BooleanSetting.builder().id("aimbot_target_mobs")
			.displayName("Target Mobs").description("Target mobs.").defaultValue(false).build();

	private final BooleanSetting targetPlayers = BooleanSetting.builder().id("aimbot_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

	private final BooleanSetting targetFriends = BooleanSetting.builder().id("aimbot_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(true).build();

	private final FloatSetting frequency = FloatSetting.builder().id("aimbot_frequency").displayName("Ticks")
			.description("How frequent the aimbot updates (Lower = Laggier)").defaultValue(1.0f).minValue(1.0f)
			.maxValue(1.0f).step(1.0f).build();

	private final FloatSetting radius = FloatSetting.builder().id("aimbot_radius").displayName("Radius")
			.description("Radius that the aimbot will lock onto a target.").defaultValue(64.0f).minValue(1.0f)
			.maxValue(256.0f).step(1.0f).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("aimbot_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private final FloatSetting maxRotation = FloatSetting.builder().id("aimbot_max_rotation")
			.displayName("Max Rotation").description("The max speed that Aimbot will rotate").defaultValue(10.0f)
			.minValue(1.0f).maxValue(360.0f).build();

	private final FloatSetting yawRandomness = FloatSetting.builder().id("aimbot_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final FloatSetting pitchRandomness = FloatSetting.builder().id("aimbot_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final BooleanSetting fakeRotation = BooleanSetting.builder().id("aimbot_fake_rotation")
			.displayName("Fake Rotation")
			.description("Spoofs the client's rotation so that the player appears rotated on the server")
			.defaultValue(false).build();

	private int currentTick = 0;

	public Aimbot() {
		super("Aimbot");
		setCategory(Category.of("Combat"));
		setDescription("Locks your crosshair towards a desired player or entity.");

		addSetting(targetAnimals);
		addSetting(targetPlayers);
		addSetting(targetFriends);
		addSetting(frequency);
		addSetting(radius);
		addSetting(rotationMode);
		addSetting(maxRotation);
		addSetting(yawRandomness);
		addSetting(pitchRandomness);
		addSetting(fakeRotation);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().rotationManager.setGoal(null);
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
		currentTick++;

		float radiusSqr = radius.getValueSqr();
		if (currentTick >= frequency.getValue()) {
			LivingEntity entityFound = null;

			// Check for players within range of the player.
			if (targetPlayers.getValue()) {
				for (AbstractClientPlayerEntity entity : MC.world.getPlayers()) {
					// Skip player if targetFriends is false and the FriendsList contains the
					// entity.
					if (entity == MC.player)
						continue;

					if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(entity))
						continue;

					if (entityFound == null)
						entityFound = entity;
					else {
						double entityDistanceToPlayer = entity.squaredDistanceTo(MC.player);
						if (entityDistanceToPlayer < entityFound.squaredDistanceTo(MC.player)
								&& entityDistanceToPlayer < radiusSqr) {
							entityFound = entity;
						}
					}
				}
			}

			if (targetAnimals.getValue()) {
				for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
					if (entity instanceof LivingEntity) {
						if (entity instanceof ClientPlayerEntity)
							continue;

						double entityDistanceToPlayer = entity.squaredDistanceTo(MC.player);
						if (entityDistanceToPlayer >= radiusSqr)
							continue;

						if (entityFound == null)
							entityFound = (LivingEntity) entity;
						else if (entityDistanceToPlayer < entityFound.squaredDistanceTo(MC.player)) {
							entityFound = (LivingEntity) entity;
						}
					}
				}
			}

			if (entityFound != null) {
				EntityGoal rotation = EntityGoal.builder().goal(entityFound).mode(rotationMode.getValue())
						.maxRotation(maxRotation.getValue()).pitchRandomness(pitchRandomness.getValue())
						.yawRandomness(yawRandomness.getValue()).fakeRotation(fakeRotation.getValue()).build();
				Aoba.getInstance().rotationManager.setGoal(rotation);
			} else {
				Aoba.getInstance().rotationManager.setGoal(null);
			}

			currentTick = 0;
		} else {
			if (temp != null && temp.squaredDistanceTo(MC.player) >= radiusSqr) {
				temp = null;
			}
		}
	}
}