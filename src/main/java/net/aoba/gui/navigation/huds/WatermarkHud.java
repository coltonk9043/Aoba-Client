/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.huds;

import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.ResizeMode;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;

public class WatermarkHud extends HudWindow {

	public WatermarkHud(int x, int y) {
		super("WatermarkHud", x, y, 50, 24);
		minWidth = 50f;
		minHeight = 20f;
		maxHeight = 20f;
		resizeMode = ResizeMode.None;
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (isVisible()) {
			Rectangle pos = position.getValue();
			if (pos.isDrawable()) {
				String watermarkText = "Aoba Client";
				Render2D.drawString(drawContext, watermarkText, pos.getX(), pos.getY(),
						GuiManager.foregroundColor.getValue().getColorAsInt());
			}
		}

		super.draw(drawContext, partialTicks);
	}

}
