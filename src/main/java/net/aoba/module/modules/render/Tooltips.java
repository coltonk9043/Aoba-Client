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
import net.aoba.settings.types.BooleanSetting;

public class Tooltips extends Module {

	private final BooleanSetting storage = BooleanSetting.builder().id("tooltips_storage").displayName("Storage")
			.description("Renders the contents of the storage item.").defaultValue(true).build();

	private final BooleanSetting maps = BooleanSetting.builder().id("tooltips_maps").displayName("Maps")
			.description("Render a map preview").defaultValue(true).build();

	public Tooltips() {
		super("Tooltips");
		setCategory(Category.of("Render"));
		setDescription("Renders custom item tooltips");

		addSetting(storage);
		addSetting(maps);
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

	public boolean getStorage() {
		return storage.getValue();
	}

	public boolean getMap() {
		return maps.getValue();
	}
}
