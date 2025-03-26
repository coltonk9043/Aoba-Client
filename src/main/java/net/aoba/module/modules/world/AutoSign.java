/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.world;

import net.aoba.managers.CommandManager;
import net.aoba.module.Category;
import net.aoba.module.Module;

public class AutoSign extends Module {
	String[] text;

	public AutoSign() {
		super("AutoSign");
		setCategory(Category.of("World"));
		setDescription("Automatically places sign with predefined text.");
	}

	public void setText(String[] text) {
		this.text = text;
	}

	public String[] getText() {
		return text;
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
		CommandManager.sendChatMessage("Place down a sign to set text!");
		text = null;
	}

	@Override
	public void onToggle() {
	}
}
