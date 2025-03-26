/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.misc;

import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;

public class FastBreak extends Module {

	private final FloatSetting multiplier = FloatSetting.builder().id("fastbreak_multiplier").displayName("Multiplier")
			.description("Multiplier for how fast the blocks will break.").defaultValue(1.25f).minValue(1.0f)
			.maxValue(10.0f).step(0.05f).build();

	private final BooleanSetting ignoreWater = BooleanSetting.builder().id("fastbreak_ignore_water")
			.displayName("Ignore Water").description("Ignores the slowdown that being in water causes.")
			.defaultValue(false).build();

	public FastBreak() {
		super("FastBreak");

		setCategory(Category.of("Misc"));
		setDescription("Breaks blocks quicker based on a multiplier.");

		addSetting(multiplier);
		addSetting(ignoreWater);

		setDetectable(
				AntiCheat.NoCheatPlus,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Grim,
				AntiCheat.Matrix
		);
	}

	public float getMultiplier() {
		return multiplier.getValue();
	}

	public boolean shouldIgnoreWater() {
		return ignoreWater.getValue();
	}

	public void setMultiplier(float multiplier) {
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
