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
 * FastBreak Module
 */
package net.aoba.module.modules.misc;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;

public class FastBreak extends Module {

	public FloatSetting multiplier = FloatSetting.builder().id("fastbreak_multiplier").displayName("Multiplier")
			.description("Multiplier for how fast the blocks will break.").defaultValue(1.25f).minValue(1.0f)
			.maxValue(10.0f).step(0.05f).build();

	public BooleanSetting ignoreWater = BooleanSetting.builder().id("fastbreak_ignore_water")
			.displayName("Ignore Water").description("Ignores the slowdown that being in water causes.")
			.defaultValue(false).build();

	public FastBreak() {
		super("FastBreak");

		this.setCategory(Category.of("Misc"));
		this.setDescription("Breaks blocks quicker based on a multiplier.");

		this.addSetting(multiplier);
		this.addSetting(ignoreWater);
	}

	public void setMultiplier(float multiplier) {
		this.multiplier.setValue(multiplier);
	}

	public float getMultiplier() {
		return this.multiplier.getValue().floatValue();
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
