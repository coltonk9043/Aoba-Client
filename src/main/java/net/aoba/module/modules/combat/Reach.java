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
 * Reach Module
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