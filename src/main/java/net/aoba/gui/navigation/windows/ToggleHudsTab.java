/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.windows;

import java.util.ArrayList;

import net.aoba.gui.Margin;
import net.aoba.gui.components.HudComponent;
import net.aoba.gui.components.SeparatorComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.navigation.Window;
import net.minecraft.client.gui.DrawContext;

public class ToggleHudsTab extends Window {
	public ToggleHudsTab(ArrayList<HudWindow> huds) {
		super("Toggle HUDs", 0, 0);

		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setMargin(new Margin(null, 30f, null, null));

		stackPanel.addChild(new StringComponent("Toggle HUDs"));
		stackPanel.addChild(new SeparatorComponent());

		for (HudWindow hud : huds) {
			HudComponent hudComponent = new HudComponent(hud.getID(), hud);
			stackPanel.addChild(hudComponent);
		}

		addChild(stackPanel);
		setMinWidth(300.0f);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
	}
}
