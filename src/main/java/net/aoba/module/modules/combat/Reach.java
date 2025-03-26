/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;

public class Reach extends Module {

	private final FloatSetting distance = FloatSetting.builder().id("reach_distance").displayName("Distance")
			.description("Distance, in blocks, that you can reach.").defaultValue(5f).minValue(1f).maxValue(128f)
			.step(1f).build();

    public Reach() {
		super("Reach");

		setCategory(Category.of("Combat"));
		setDescription("Allows you to reach further.");

		addSetting(distance);

		setDetectable(
				AntiCheat.NoCheatPlus,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Grim,
				AntiCheat.Buzz
		);
	}

	public float getReach() {
		return distance.getValue().floatValue();
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

	public void setReachLength(float reach) {
		distance.setValue(reach);
	}
}