/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;

public class Reach extends Module {

	private FloatSetting distance;

	public Reach() {
		super("Reach");

		this.setCategory(Category.of("Combat"));
		this.setDescription("Allows you to reach further.");

		distance = FloatSetting.builder().id("reach_distance").displayName("Distance")
				.description("Distance, in blocks, that you can reach.").defaultValue(5f).minValue(1f).maxValue(128f)
				.step(1f).build();
		this.addSetting(distance);
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
		this.distance.setValue(reach);
	}
}