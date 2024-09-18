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

package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
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
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class TriggerBot extends Module implements TickListener {
    private FloatSetting radius;
    private BooleanSetting targetAnimals;
    private BooleanSetting targetMonsters;
    private BooleanSetting targetPlayers;
    private FloatSetting attackDelay;

    private long lastAttackTime;

    public TriggerBot() {
        super(new KeybindSetting("key.triggerbot", "TriggerBot Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("Triggerbot");
        this.setCategory(Category.of("Combat"));
        this.setDescription("Attacks anything you are looking at.");

        radius = new FloatSetting("triggerbot_radius", "Radius", 5f, 0.1f, 10f, 0.1f);
        targetAnimals = new BooleanSetting("triggerbot_target_animals", "Target Animals", "Target animals.", false);
        targetMonsters = new BooleanSetting("triggerbot_target_monsters", "Target Monsters", "Target monsters.", true);
        targetPlayers = new BooleanSetting("triggerbot_target_players", "Target Players", "Target players.", true);
        attackDelay = new FloatSetting("triggerbot_attack_delay", "Attack Delay", "Delay in milliseconds between attacks.", 0, 0, 500, 10);

        this.addSetting(attackDelay);
        this.addSetting(radius);
        this.addSetting(targetAnimals);
        this.addSetting(targetMonsters);
        this.addSetting(targetPlayers);

        this.lastAttackTime = 0L;
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
		if (MC.player.getAttackCooldownProgress(0) == 1) {
            HitResult ray = MC.crosshairTarget;

            if (ray != null && ray.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityResult = (EntityHitResult) ray;
                Entity ent = entityResult.getEntity();

                if (!(ent instanceof LivingEntity)) {
                    return;
                }

                if (ent instanceof AnimalEntity && !this.targetAnimals.getValue())
                    return;
                if (ent instanceof PlayerEntity && !this.targetPlayers.getValue())
                    return;
                if (ent instanceof Monster && !this.targetMonsters.getValue())
                    return;

                if (System.currentTimeMillis() - this.lastAttackTime >= attackDelay.getValue()) {
                    MC.interactionManager.attackEntity(MC.player, entityResult.getEntity());
                    MC.player.swingHand(Hand.MAIN_HAND);
                    this.lastAttackTime = System.currentTimeMillis();
                }
            }
        }
	}

	@Override
	public void onTick(Post event) {

	}
}
