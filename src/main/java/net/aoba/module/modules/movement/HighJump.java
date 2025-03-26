/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.movement;

import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;

public class HighJump extends Module {

	private final FloatSetting multiplier = FloatSetting.builder().id("highjump_jumpmultiplier")
			.displayName("Jump Multiplier").description("The height that the player will jump.").defaultValue(1.5f)
			.minValue(0.1f).maxValue(10f).step(0.1f).build();

	public HighJump() {
		super("HighJump");
		setCategory(Category.of("Movement"));
		setDescription("Allows the player to jump super high!");

		addSetting(multiplier);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Verus,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity,
		    AntiCheat.Karhu,
		    AntiCheat.Buzz
		);
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

	public float getJumpHeightMultiplier() {
		return multiplier.getValue();
	}
}