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

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

enum MaceState {
    OnGround, InAir, Descending,
}

public class MaceAura extends Module implements TickListener {
    private FloatSetting radius;
    private BooleanSetting targetAnimals;
    private BooleanSetting targetMonsters;
    private BooleanSetting targetPlayers;
    private BooleanSetting targetFriends;
    private MaceState state = MaceState.OnGround;
    private LivingEntity entityToAttack;

    public MaceAura() {
        super(new KeybindSetting("key.maceaura", "Mace Aura Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("MaceAura");
        this.setCategory(Category.of("Combat"));
        this.setDescription("Smashes players in your personal space with a Mace with extreme damage. Be sure to enable NoFall for best results.");

        radius = new FloatSetting("maceaura_radius", "Radius", "Radius", 5f, 0.1f, 10f, 0.1f);
        targetAnimals = new BooleanSetting("maceaura_target_animals", "Target Animals", "Target animals.", false);
        targetMonsters = new BooleanSetting("maceaura_target_monsters", "Target Monsters", "Target monsters.", true);
        targetPlayers = new BooleanSetting("maceaura_target_players", "Target Players", "Target pplayers.", true);
        targetFriends = new BooleanSetting("maceaura_target_friends", "Target Friends", "Target friends.", false);

        this.addSetting(radius);
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
    public void OnUpdate(TickEvent event) {
        if (state == MaceState.OnGround) {
            if (MC.player.getMainHandStack().getItem() == Items.MACE && MC.player.getAttackCooldownProgress(0) == 1) {
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

                // If the entity is found, we want to attach it.
                if (entityToAttack != null) {
                    Vec3d velocity = MC.player.getVelocity().add(0, 20, 0);
                    MC.player.setVelocity(velocity);
                    state = MaceState.InAir;
                }
            }
        } else if (state == MaceState.InAir) {
            Vec3d velocity = MC.player.getVelocity().add(0, -39, 0);
            MC.player.setVelocity(velocity);
            state = MaceState.Descending;
        } else if (state == MaceState.Descending) {
            MC.interactionManager.attackEntity(MC.player, entityToAttack);
            MC.player.swingHand(Hand.MAIN_HAND);
            entityToAttack = null;
            Vec3d velocity = MC.player.getVelocity().add(0, 39, 0);
            MC.player.setVelocity(velocity);
            state = MaceState.OnGround;
        }
    }
}
