/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.windows;

import net.aoba.Aoba;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.components.EnumComponent;
import net.aoba.gui.components.SeparatorComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.navigation.Window;
import net.aoba.module.AntiCheat;

/**
 * Represents the AntiCheat Window that allows the user to select their
 * anticheat.
 */
public class AntiCheatWindow extends Window {
	public AntiCheatWindow() {
		super("AntiCheat", 50, 895);
		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.addChild(new StringComponent("AntiCheat Settings"));
		stackPanel.addChild(new SeparatorComponent());
		stackPanel.addChild(new EnumComponent<AntiCheat>(Aoba.getInstance().moduleManager.antiCheat));
		stackPanel.addChild(new StringComponent(
				"The selected AC will disable any features that are KNOWN detectable by that AC.", Colors.Gray, false));
		addChild(stackPanel);
		setMinWidth(300.0f);
	}
}
