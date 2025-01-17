/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
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
