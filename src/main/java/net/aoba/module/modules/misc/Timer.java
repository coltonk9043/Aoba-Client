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

		this.setDetectable(AntiCheat.NoCheatPlus);
		this.setDetectable(AntiCheat.Vulcan);
		this.setDetectable(AntiCheat.AdvancedAntiCheat);
		this.setDetectable(AntiCheat.Verus);
		this.setDetectable(AntiCheat.Grim);
		this.setDetectable(AntiCheat.Matrix);
		this.setDetectable(AntiCheat.Negativity);
		this.setDetectable(AntiCheat.Karhu);
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