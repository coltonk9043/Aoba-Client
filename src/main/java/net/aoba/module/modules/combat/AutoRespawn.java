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
 * AutoRespawn Module
 */
package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.PlayerDeathEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.PlayerDeathListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;

public class AutoRespawn extends Module implements PlayerDeathListener, TickListener {

    private FloatSetting respawnDelay;

    private int tick;

    public AutoRespawn() {
    	super("AutoRespawn");

        this.setCategory(Category.of("Combat"));
        this.setDescription("Automatically respawns when you die.");

        respawnDelay = FloatSetting.builder()
        		.id("autorespawn_delay")
        		.displayName("Delay")
        		.description("The delay between dying and automatically respawning.")
        		.defaultValue(0.0f)
        		.minValue(0.0f)
        		.maxValue(100.0f)
        		.step(1.0f)
        		.build();
        		
        this.addSetting(respawnDelay);
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
        Aoba.getInstance().eventManager.RemoveListener(PlayerDeathListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(PlayerDeathListener.class, this);
    }

    @Override
    public void onToggle() {

    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent readPacketEvent) {
        if (respawnDelay.getValue() == 0.0f) {
            respawn();
        } else {
            tick = 0;
            Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
        }
    }

    @Override
    public void onTick(TickEvent.Pre event) {

    }
    
    @Override
    public void onTick(TickEvent.Post event) {
        if (tick < respawnDelay.getValue()) {
            tick++;
        } else {
            respawn();
        }
    }

    private void respawn() {
        MC.player.requestRespawn();
        MC.setScreen(null);
        Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
    }
}
