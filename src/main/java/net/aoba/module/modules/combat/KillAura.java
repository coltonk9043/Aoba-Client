/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.BooleanSetting;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Hand;

public class KillAura extends Module {
	private enum Priority {
		LOWESTHP, CLOSEST
	}

	private Priority priority = Priority.LOWESTHP;
	private SliderSetting radius;
	private BooleanSetting targetAnimals;
	private BooleanSetting targetMonsters;
	private BooleanSetting targetPlayers;
	
	public KillAura() {
		this.setName("KillAura");
		this.setBind(new KeyBinding("key.killaura", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Attacks anything within your personal space.");
		
		radius = new SliderSetting("Radius", "killaura_radius", 5f, 0.1f, 10f, 0.1f);
		targetAnimals = new BooleanSetting("Trgt Mobs", "killaura_target_animals");
		targetMonsters = new BooleanSetting("Trgt Monsters", "killaura_target_monsters");
		targetPlayers = new BooleanSetting("Trgt Players", "killaura_target_players");
		this.addSetting(radius);
		this.addSetting(targetAnimals);
		this.addSetting(targetMonsters);
		this.addSetting(targetPlayers);
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		if(MC.player.getAttackCooldownProgress(0) == 1) {
			ArrayList<Entity> hitList = new ArrayList<Entity>();
			LivingEntity entityToAttack = null;
			boolean found = false;
			
			// Add all potential entities to the 'hitlist'
			
				for (Entity entity : MC.world.getEntities()) {
					if (MC.player.squaredDistanceTo(entity) > (this.radius.getValue()*this.radius.getValue())) continue;
					if((entity instanceof AnimalEntity && this.targetAnimals.getValue()) || (entity instanceof Monster && this.targetMonsters.getValue())) {
						hitList.add(entity);
					}	
				}
			
			
			// Add all potential players to the 'hitlist'
			if(this.targetPlayers.getValue()) {
				for (PlayerEntity player : MC.world.getPlayers()) {
					if (player == MC.player || MC.player.squaredDistanceTo(player) > (this.radius.getValue()*this.radius.getValue())) {
						continue;
					}
					hitList.add(player);
				}
			}
			
			for (Entity entity : hitList) {
				LivingEntity le = (LivingEntity) entity;
				if (entityToAttack == null) {
					entityToAttack = le;
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
			
			if (found) {
				MC.interactionManager.attackEntity(MC.player, entityToAttack);
				MC.player.swingHand(Hand.MAIN_HAND);
			}
		}
	}


	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {

	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
}
