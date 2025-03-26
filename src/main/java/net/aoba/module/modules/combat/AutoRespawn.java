/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
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

	private final FloatSetting respawnDelay = FloatSetting.builder().id("autorespawn_delay").displayName("Delay")
			.description("The delay between dying and automatically respawning.").defaultValue(0.0f).minValue(0.0f)
			.maxValue(100.0f).step(1.0f).build();

    private int tick;

	public AutoRespawn() {
		super("AutoRespawn");

		setCategory(Category.of("Combat"));
		setDescription("Automatically respawns when you die.");

		addSetting(respawnDelay);
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
		readPacketEvent.cancel();
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
