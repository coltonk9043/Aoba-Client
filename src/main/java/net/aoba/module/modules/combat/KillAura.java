/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * KillAura Module
 */
package net.aoba.module.modules.combat;

import java.util.ArrayList;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.rotation.Rotation;
import net.aoba.utils.rotation.RotationMode;
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class KillAura extends Module implements TickListener {
	private enum Priority {
		LOWESTHP, CLOSEST
	}

	private Priority priority = Priority.CLOSEST;

	private FloatSetting radius = FloatSetting.builder().id("killaura_radius").displayName("Radius")
			.description("Radius that KillAura will target entities.").defaultValue(5f).minValue(0.1f).maxValue(10f)
			.step(0.1f).build();

	private BooleanSetting targetAnimals = BooleanSetting.builder().id("killaura_target_animals")
			.displayName("Target Animals").description("Target animals.").defaultValue(false).build();

	private BooleanSetting targetMonsters = BooleanSetting.builder().id("killaura_target_monsters")
			.displayName("Target Monsters").description("Target Monsters.").defaultValue(true).build();

	private BooleanSetting targetPlayers = BooleanSetting.builder().id("killaura_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

	private BooleanSetting targetFriends = BooleanSetting.builder().id("killaura_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(false).build();

	private FloatSetting randomness = FloatSetting.builder().id("killaura_randomness").displayName("Randomness")
			.description("The randomness of the delay between when KillAura will hit a target.").defaultValue(0.0f)
			.minValue(0.0f).maxValue(60.0f).step(1.0f).build();

	private BooleanSetting legit = BooleanSetting.builder().id("killaura_legit").displayName("Legit")
			.description(
					"Whether a raycast will be used to ensure that KillAura will not hit a player outside of the view")
			.defaultValue(false).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("killaura_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private FloatSetting maxRotation = FloatSetting.builder().id("killaura_max_rotation").displayName("Max Rotation")
			.description("The max speed that KillAura will rotate").defaultValue(10.0f).minValue(1.0f).maxValue(360.0f)
			.build();

	private FloatSetting yawRandomness = FloatSetting.builder().id("killaura_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(60.0f).step(1.0f).build();

	private FloatSetting pitchRandomness = FloatSetting.builder().id("killaura_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(60.0f).step(1.0f).build();

	private LivingEntity entityToAttack;

	public KillAura() {
		super("KillAura");

		this.setCategory(Category.of("Combat"));
		this.setDescription("Attacks anything within your personal space.");

		this.addSetting(radius);
		this.addSetting(targetAnimals);
		this.addSetting(targetMonsters);
		this.addSetting(targetPlayers);
		this.addSetting(targetFriends);
		this.addSetting(legit);
		this.addSetting(randomness);
		this.addSetting(rotationMode);
		this.addSetting(maxRotation);
		this.addSetting(yawRandomness);
		this.addSetting(pitchRandomness);
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
		int randomnessValue = randomness.getValue().intValue();
		boolean state = randomnessValue == 0
				|| (Math.round(Math.random() * Math.round(randomness.max_value))) % randomnessValue == 0;

		ArrayList<Entity> hitList = new ArrayList<Entity>();
		boolean found = false;

		// Add all potential entities to the 'hitlist'
		if (this.targetAnimals.getValue() || this.targetMonsters.getValue()) {
			for (Entity entity : MC.world.getEntities()) {
				if (MC.player.squaredDistanceTo(entity) > radius.getValueSqr())
					continue;
				if ((entity instanceof AnimalEntity && this.targetAnimals.getValue())
						|| (entity instanceof Monster && this.targetMonsters.getValue())) {
					hitList.add(entity);
				}
			}
		}

		// Add all potential players to the 'hitlist'
		if (this.targetPlayers.getValue()) {
			for (PlayerEntity player : MC.world.getPlayers()) {
				if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(player))
					continue;

				if (player == MC.player || MC.player.squaredDistanceTo(player) > (radius.getValueSqr())) {
					continue;
				}
				hitList.add(player);
			}
		}

		// For each entity, get the entity that matches a criteria.
		for (Entity entity : hitList) {
			LivingEntity le = (LivingEntity) entity;
			if (entityToAttack == null) {
				entityToAttack = le;
				found = true;
			} else {
				if (this.priority == Priority.LOWESTHP) {
					if (le.getHealth() <= entityToAttack.getHealth()) {
						entityToAttack = le;
						found = true;
					}
				} else if (this.priority == Priority.CLOSEST) {
					if (MC.player.squaredDistanceTo(le) <= MC.player.squaredDistanceTo(entityToAttack)) {
						entityToAttack = le;
						found = true;
					}
				}
			}
		}

		// If the entity is found, we want to attach it.
		if (found) {
			switch (rotationMode.getValue()) {
			case RotationMode.NONE:
				break;
			case RotationMode.SMOOTH:
				float rotationDegreesPerTick = maxRotation.getValue();
				Rotation rotation = Rotation.getPlayerRotationDeltaFromEntity(entityToAttack);

				float maxYawRotationDelta = Math.clamp((float) -rotation.yaw(), -rotationDegreesPerTick,
						rotationDegreesPerTick);
				float maxPitchRotation = Math.clamp((float) -rotation.pitch(), -rotationDegreesPerTick,
						rotationDegreesPerTick);

				// Apply Pitch / Yaw randomness and
				double pitchRandom = Math.random() * pitchRandomness.getValue();
				double yawRandom = Math.random() * yawRandomness.getValue();

				maxYawRotationDelta += yawRandom;
				maxPitchRotation += pitchRandom;

				Rotation newRotation = new Rotation(MC.player.getYaw() + maxYawRotationDelta,
						MC.player.getPitch() + maxPitchRotation).roundToGCD();
				MC.player.setYaw((float) newRotation.yaw());
				MC.player.setPitch((float) newRotation.pitch());
				break;
			case RotationMode.INSTANT:
				MC.player.lookAt(EntityAnchor.EYES, entityToAttack.getEyePos());
				break;
			}

			if (MC.player.getAttackCooldownProgress(0) == 1) {

				if (state) {
					if (legit.getValue()) {
						HitResult ray = MC.crosshairTarget;

						if (ray != null && ray.getType() == HitResult.Type.ENTITY) {
							EntityHitResult entityResult = (EntityHitResult) ray;
							Entity ent = entityResult.getEntity();

							if (ent == entityToAttack) {
								MC.player.swingHand(Hand.MAIN_HAND);
								MC.interactionManager.attackEntity(MC.player, entityToAttack);
							}
						}
					} else {
						MC.player.swingHand(Hand.MAIN_HAND);
						MC.interactionManager.attackEntity(MC.player, entityToAttack);
					}
				}
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
