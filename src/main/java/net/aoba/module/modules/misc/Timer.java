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
 * Timer Module
 */
package net.aoba.module.modules.misc;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;

public class Timer extends Module {
	private final FloatSetting multiplier = FloatSetting.builder().id("timer_multiplier").displayName("Multiplier")
			.description("The multiplier that will affect the game speed.").defaultValue(1f).minValue(0.1f)
			.maxValue(15.0f).step(0.1f).build();

	public Timer() {
		super("Timer");

		this.setCategory(Category.of("Misc"));
		this.setDescription("Increases the speed of Minecraft.");
		this.addSetting(multiplier);
	}

	public float getMultiplier() {
		return this.multiplier.getValue().floatValue();
	}

	public void setMultipler(float multiplier) {
		this.multiplier.setValue(multiplier);
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
}