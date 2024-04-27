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
import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec2f;

public class KillAura extends Module implements TickListener {
	private enum Priority {
		LOWESTHP, CLOSEST
	}

	private Priority priority = Priority.CLOSEST;
	private FloatSetting radius;
	private BooleanSetting targetAnimals;
	private BooleanSetting targetMonsters;
	private BooleanSetting targetPlayers;
	private BooleanSetting legit;
	
	public KillAura() {
		super(new KeybindSetting("key.killaura", "Kill Aura Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("KillAura");
		this.setCategory(Category.Combat);
		this.setDescription("Attacks anything within your personal space.");
		
		radius = new FloatSetting("killaura_radius", "Radius", "Radius", 5f, 0.1f, 10f, 0.1f);
		targetAnimals = new BooleanSetting("killaura_target_animals", "Target Animals", "Target animals.", false);
		targetMonsters = new BooleanSetting("killaura_target_monsters", "Target Monsters", "Target monsters.", true);
		targetPlayers = new BooleanSetting("killaura_target_players", "Target Players", "Target pplayers.", true);
		legit = new BooleanSetting("killaura_legit", "Legit", "Whether or not the player will gradually look at the other player.", false);
		this.addSetting(radius);
		this.addSetting(targetAnimals);
		this.addSetting(targetMonsters);
		this.addSetting(targetPlayers);
		this.addSetting(legit);
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
			//if(this.targetAnimals.getValue() || this.targetMonsters.getValue()) {
			//	double radiusSqr = this.radius.getValue() * this.radius.getValue();
			//	for (Entity entity : MC.world.getEntities()) {
			//		if (MC.player.squaredDistanceTo(entity) > radiusSqr) continue;
			//		if((entity instanceof AnimalEntity && this.targetAnimals.getValue()) || (entity instanceof Monster && this.targetMonsters.getValue())) {
			//			hitList.add(entity);
			//		}	
			//	}
			//}

			// Add all potential players to the 'hitlist'
			if(this.targetPlayers.getValue()) {
				for (PlayerEntity player : MC.world.getPlayers()) {
					if (player == MC.player || MC.player.squaredDistanceTo(player) > (this.radius.getValue()*this.radius.getValue())) {
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
				if(legit.getValue()) {
					float camPitch = MC.cameraEntity.getPitch();
					float camYaw = MC.cameraEntity.getYaw();
					
					Vec2f vec2 = new Vec2f((float)(MC.player.getX() - entityToAttack.getX()), (float)(MC.player.getZ() - entityToAttack.getZ()));
					vec2 = vec2.normalize();
					
					double angleFromPlayer = Math.atan2(vec2.y, vec2.x);
				}
				MC.interactionManager.attackEntity(MC.player, entityToAttack);
				MC.player.swingHand(Hand.MAIN_HAND);
			}
		}
	}
}
