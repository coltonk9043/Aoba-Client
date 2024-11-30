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
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class MaceAura extends Module implements TickListener {
	private FloatSetting radius = FloatSetting.builder().id("maceaura_radius").displayName("Radius")
			.description("Radius that MaceAura will trigger").defaultValue(5f).minValue(0.1f).maxValue(10f).step(0.1f)
			.build();

	private FloatSetting height = FloatSetting.builder().id("maceaura_height").displayName("Height")
			.description("Determines how high MaceAura will jump. Higher distance = more damage.").defaultValue(100f)
			.minValue(1f).maxValue(255f).build();

	private BooleanSetting targetAnimals = BooleanSetting.builder().id("maceaura_target_animals")
			.displayName("Target Animals").description("Target animals.").defaultValue(false).build();

	private BooleanSetting targetMonsters = BooleanSetting.builder().id("maceaura_target_monsters")
			.displayName("Target Monsters").description("Target Monsters.").defaultValue(true).build();

	private BooleanSetting targetPlayers = BooleanSetting.builder().id("maceaura_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

	private BooleanSetting targetFriends = BooleanSetting.builder().id("maceaura_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(false).build();

	private LivingEntity entityToAttack;

	public MaceAura() {
		super("MaceAura");

		this.setCategory(Category.of("Combat"));
		this.setDescription(
				"Smashes players in your personal space with a Mace with extreme damage. Be sure to enable NoFall for best results.");

		this.addSetting(radius);
		this.addSetting(height);
		this.addSetting(targetAnimals);
		this.addSetting(targetMonsters);
		this.addSetting(targetPlayers);
		this.addSetting(targetFriends);
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
		if (MC.player.getMainHandStack().getItem() == Items.MACE && MC.player.getAttackCooldownProgress(0) == 1) {

			if (entityToAttack == null) {
				ArrayList<Entity> hitList = new ArrayList<Entity>();

				// Add all potential entities to the 'hitlist'
				if (this.targetAnimals.getValue() || this.targetMonsters.getValue()) {
					for (Entity entity : MC.world.getEntities()) {
						if (entity == MC.player)
							continue;
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

						if (player == MC.player || MC.player
								.squaredDistanceTo(player) > (this.radius.getValue() * this.radius.getValue())) {
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
					} else {
						if (MC.player.squaredDistanceTo(le) <= MC.player.squaredDistanceTo(entityToAttack)) {
							entityToAttack = le;
						}
					}
				}

				if (entityToAttack != null) {
					// If the entity is found, we want to attach it.
					int packetsRequired = Math.round((float) Math.ceil(Math.abs(height.getValue() / 10.0f)));
					for (int i = 0; i < packetsRequired; i++) {
						MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false, false));
					}

					Vec3d newPos = MC.player.getPos().add(0, height.getValue(), 0);
					MC.player.networkHandler.sendPacket(
							new PlayerMoveC2SPacket.PositionAndOnGround(newPos.x, newPos.y, newPos.z, false, false));
				}
			} else {
				int packetsRequired = Math.round((float) Math.ceil(Math.abs(height.getValue() / 10.0f)));
				for (int i = 0; i < packetsRequired; i++) {
					MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false, false));
				}

				Vec3d newPos = MC.player.getPos();
				MC.player.networkHandler.sendPacket(
						new PlayerMoveC2SPacket.PositionAndOnGround(newPos.x, newPos.y, newPos.z, false, false));

				MC.interactionManager.attackEntity(MC.player, entityToAttack);
				MC.player.swingHand(Hand.MAIN_HAND);
				entityToAttack = null;
			}
		}
	}
}
