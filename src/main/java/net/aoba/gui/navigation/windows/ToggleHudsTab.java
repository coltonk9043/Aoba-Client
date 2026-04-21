/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.windows;

import java.util.ArrayList;
import net.aoba.gui.UIElement;
import net.aoba.gui.components.HudComponent;
import net.aoba.gui.components.SeparatorComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.font.FontManager;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.navigation.Window;
import net.aoba.gui.types.SizeToContent;
import net.aoba.gui.GuiManager;

public class ToggleHudsTab extends Window {
	public ToggleHudsTab(ArrayList<HudWindow> huds) {
		super("Toggle HUDs", 0, 0);
		sizeToContent = SizeToContent.Both;
		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(8f);
		
		StringComponent header = new StringComponent("Toggle HUDs");
		header.setProperty(UIElement.FontWeightProperty, FontManager.WEIGHT_BOLD);
		header.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundHeaderColor);
		stackPanel.addChild(header);
		
		stackPanel.addChild(new SeparatorComponent());

		for (HudWindow hud : huds) {
			HudComponent hudComponent = new HudComponent(hud.getID(), hud);
			stackPanel.addChild(hudComponent);
		}

		setContent(stackPanel);
		setProperty(UIElement.MinWidthProperty, 300f);
	}
}
