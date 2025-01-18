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
 * FastPlace Module
 */
package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;

public class FastPlace extends Module implements TickListener {

	private FloatSetting speed = FloatSetting.builder().id("fastplace_delay").displayName("Delay")
			.description("Delay at which blocks are placed in ticks..").defaultValue(0f).minValue(0f).maxValue(5f)
			.step(1f).build();

	public FastPlace() {
		super("FastPlace");

		this.setCategory(Category.of("Misc"));
		this.setDescription("Places blocks exceptionally fast");

		this.addSetting(speed);
	}

	@Override
	public void onDisable() {
		IMC.setItemUseCooldown(5);
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
		int currentItemCooldown = IMC.getItemUseCooldown();
		int speedValue = speed.getValue().intValue();
		if (currentItemCooldown == 0 || currentItemCooldown > speedValue)
			IMC.setItemUseCooldown(speedValue);
	}

	@Override
	public void onTick(Post event) {

	}
}
