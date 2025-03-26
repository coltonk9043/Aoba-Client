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

public class TimeHud extends HudWindow {
	private String timeText = null;

	public TimeHud(int x, int y) {
		super("TimeHud", x, y, 80, 24);
		minWidth = 80f;
		minHeight = 20f;
		maxHeight = 20f;
		resizeMode = ResizeMode.None;
	}

	@Override
	public void update() {
		super.update();
		int time = ((int) MC.world.getTime() + 6000) % 24000;
		String suffix = time >= 12000 ? "PM" : "AM";
		StringBuilder timeString = new StringBuilder((time / 10) % 1200 + "");
		for (int n = timeString.length(); n < 4; ++n) {
			timeString.insert(0, "0");
		}
		String[] strsplit = timeString.toString().split("");
		String hours = strsplit[0] + strsplit[1];
		if (hours.equalsIgnoreCase("00")) {
			hours = "12";
		}
		int minutes = (int) Math.floor(Double.parseDouble(strsplit[2] + strsplit[3]) / 100.0 * 60.0);
		String sm = minutes + "";
		if (minutes < 10) {
			sm = "0" + minutes;
		}
		timeString = new StringBuilder(hours + ":" + sm.charAt(0) + sm.charAt(1) + suffix);

		timeText = timeString.toString();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (timeText != null && isVisible()) {
			Rectangle pos = position.getValue();
			if (pos.isDrawable()) {
				Render2D.drawString(drawContext, timeText, pos.getX(), pos.getY(),
						GuiManager.foregroundColor.getValue().getColorAsInt());
			}
		}

		super.draw(drawContext, partialTicks);
	}
}