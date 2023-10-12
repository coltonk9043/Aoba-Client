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
import net.aoba.Aoba;
import net.aoba.core.settings.types.BooleanSetting;
import net.aoba.core.settings.types.FloatSetting;
import net.aoba.core.settings.types.KeybindSetting;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class KillAura extends Module implements TickListener {
	private enum Priority {
		LOWESTHP, CLOSEST
	}

	private Priority priority = Priority.LOWESTHP;
	private FloatSetting radius;
	private BooleanSetting targetAnimals;
	private BooleanSetting targetMonsters;
	private BooleanSetting targetPlayers;
	
	public KillAura() {
		super(new KeybindSetting("key.killaura", "Kill Aura Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("KillAura");
		this.setCategory(Category.Combat);
		this.setDescription("Attacks anything within your personal space.");
		
		radius = new FloatSetting("killaura_radius", "Radius", "Radius", 5f, 0.1f, 10f, 0.1f);
		targetAnimals = new BooleanSetting("killaura_target_animals", "Target Animals", "Target animals.", false);
		targetMonsters = new BooleanSetting("killaura_target_monsters", "Target Monsters", "Target monsters.", true);
		targetPlayers = new BooleanSetting("killaura_target_players", "Target Players", "Target pplayers.", true);
		this.addSetting(radius);
		this.addSetting(targetAnimals);
		this.addSetting(targetMonsters);
		this.addSetting(targetPlayers);
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
	public void OnUpdate(TickEvent event) {
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
}
