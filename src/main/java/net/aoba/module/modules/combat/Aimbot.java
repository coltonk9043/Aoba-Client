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
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Aimbot extends Module implements TickListener, Render3DListener {

    private LivingEntity temp = null;

    private BooleanSetting targetAnimals;
    private BooleanSetting targetPlayers;
    private BooleanSetting targetFriends;
    private FloatSetting frequency;
    private FloatSetting radius;

    private int currentTick = 0;

    public Aimbot() {
        super(new KeybindSetting("key.aimbot", "Aimbot Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
        this.setName("Aimbot");

        this.setCategory(Category.Combat);
        this.setDescription("Locks your crosshair towards a desired player or entity.");

        targetAnimals = new BooleanSetting("aimbot_target_mobs", "Target Mobs", "Target mobs.", false);
        targetPlayers = new BooleanSetting("aimbot_target_players", "Target Players", "Target players.", true);
        targetFriends = new BooleanSetting("aimbot_target_friends", "Target Friends", "Target friends.", false);
        frequency = new FloatSetting("aimbot_frequency", "Ticks", "How frequent the aimbot updates (Lower = Laggier)", 1.0f, 1.0f, 20.0f, 1.0f);
        radius = new FloatSetting("aimbot_radius", "Radius", "Radius", 64.0f, 1.0f, 256.0f, 1.0f);

        this.addSetting(targetAnimals);
        this.addSetting(targetPlayers);
        this.addSetting(targetFriends);
        this.addSetting(frequency);
        this.addSetting(radius);
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
        Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
        Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void OnRender(Render3DEvent event) {
        if (temp != null) {
            Vec3d offset = RenderUtils.getEntityPositionOffsetInterpolated(temp, event.GetPartialTicks());
            MC.player.lookAt(EntityAnchor.EYES, temp.getEyePos().add(offset));
        }
    }

    @Override
    public void OnUpdate(TickEvent event) {
        currentTick++;

        float radiusSqr = radius.getValue() * radius.getValue();

        if (currentTick >= frequency.getValue()) {
            LivingEntity entityFound = null;

            // Check for players within range of the player.
            if (this.targetPlayers.getValue()) {
                for (AbstractClientPlayerEntity entity : MC.world.getPlayers()) {
                    // Skip player if targetFriends is false and the FriendsList contains the entity.
                    if (entity == MC.player)
                        continue;

                    if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(entity))
                        continue;

                    if (entityFound == null)
                        entityFound = (LivingEntity) entity;
                    else {
                        double entityDistanceToPlayer = entity.squaredDistanceTo(MC.player);
                        if (entityDistanceToPlayer < entityFound.squaredDistanceTo(MC.player) && entityDistanceToPlayer < radiusSqr) {
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

            temp = entityFound;
            currentTick = 0;
        } else {
            if (temp != null && temp.squaredDistanceTo(MC.player) >= radiusSqr) {
                temp = null;
            }
        }
    }
}