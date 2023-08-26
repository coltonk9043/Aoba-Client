/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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
 * Fullbright Module
 */
package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;
import net.aoba.interfaces.ISimpleOption;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;

public class Fullbright extends Module {

	private double previousValue = 0.0;
	public Fullbright() {
		this.setName("Fullbright");
		this.setBind(new KeyBinding("key.fullbright", GLFW.GLFW_KEY_F, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Maxes out the brightness.");

	}

	@Override
	public void onDisable() {
		@SuppressWarnings("unchecked")
		ISimpleOption<Double> gamma =
				(ISimpleOption<Double>)(Object)MC.options.getGamma();
		gamma.forceSetValue(previousValue);
	}

	@Override
	public void onEnable() {
		this.previousValue = MC.options.getGamma().getValue();
		@SuppressWarnings("unchecked")
		ISimpleOption<Double> gamma =
				(ISimpleOption<Double>)(Object)MC.options.getGamma();
		gamma.forceSetValue(10000.0);
	}

	@Override
	public void onToggle() {

	}
}
