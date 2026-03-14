/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.gui.GuiManager;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;

public class SeparatorComponent extends Component {

	public SeparatorComponent() {
		setHeight(1.0f);
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();
		float actualHeight = getActualSize().getHeight();

		Render2D.drawLine(drawContext, actualX, actualY, actualX + actualWidth, actualY + actualHeight,
				GuiManager.borderColor.getValue());
	}
}
