/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.render;

import net.aoba.module.Category;
import net.aoba.module.Module;

public class Fullbright extends Module {

	// private double previousValue = 0.0;
	public Fullbright() {
		super("Fullbright");
		setCategory(Category.of("Render"));
		setDescription("Maxes out the brightness.");
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
