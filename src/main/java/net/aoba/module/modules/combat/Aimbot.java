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
 * Aimbot Module
 */
package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.managers.rotation.goals.EntityGoal;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class Aimbot extends Module implements TickListener {

	private LivingEntity temp = null;

	private BooleanSetting targetAnimals = BooleanSetting.builder().id("aimbot_target_mobs").displayName("Target Mobs")
			.description("Target mobs.").defaultValue(false).build();

	private BooleanSetting targetPlayers = BooleanSetting.builder().id("aimbot_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

	private BooleanSetting targetFriends = BooleanSetting.builder().id("aimbot_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(true).build();

	private FloatSetting frequency = FloatSetting.builder().id("aimbot_frequency").displayName("Ticks")
			.description("How frequent the aimbot updates (Lower = Laggier)").defaultValue(1.0f).minValue(1.0f)
			.maxValue(1.0f).step(1.0f).build();

	private FloatSetting radius = FloatSetting.builder().id("aimbot_radius").displayName("Radius")
			.description("Radius that the aimbot will lock onto a target.").defaultValue(64.0f).minValue(1.0f)
			.maxValue(256.0f).step(1.0f).build();

	private FloatSetting rotationSpeed = FloatSetting.builder().id("aimbot_rotation_speed")
			.displayName("Rotation Speed").description("Speed of the rotation.").defaultValue(1.0f).minValue(0.1f)
			.maxValue(5.0f).step(0.1f).build();

	private int currentTick = 0;

	public Aimbot() {
		super("Aimbot");
		this.setCategory(Category.of("Combat"));
		this.setDescription("Locks your crosshair towards a desired player or entity.");

		this.addSetting(rotationSpeed);
		this.addSetting(targetAnimals);
		this.addSetting(targetPlayers);
		this.addSetting(targetFriends);
		this.addSetting(frequency);
		this.addSetting(radius);
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

		float radiusSqr = radius.getValue() * radius.getValue();

		if (currentTick >= frequency.getValue()) {
			LivingEntity entityFound = null;

			// Check for players within range of the player.
			if (this.targetPlayers.getValue()) {
				for (AbstractClientPlayerEntity entity : MC.world.getPlayers()) {
					// Skip player if targetFriends is false and the FriendsList contains the
					// entity.
					if (entity == MC.player)
						continue;

					if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(entity))
						continue;

					if (entityFound == null)
						entityFound = (LivingEntity) entity;
					else {
						double entityDistanceToPlayer = entity.squaredDistanceTo(MC.player);
						if (entityDistanceToPlayer < entityFound.squaredDistanceTo(MC.player)
								&& entityDistanceToPlayer < radiusSqr) {
							entityFound = entity;
						}
					}
				}
			}

			if (this.targetAnimals.getValue()) {
				for (Entity entity : MC.world.getEntities()) {
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
				EntityGoal rotation = EntityGoal.builder().goal(entityFound).maxRotation(rotationSpeed.getValue())
						.build();
				Aoba.getInstance().rotationManager.setGoal(rotation);
			}

			currentTick = 0;
		} else {
			if (temp != null && temp.squaredDistanceTo(MC.player) >= radiusSqr) {
				temp = null;
			}
		}
	}
}