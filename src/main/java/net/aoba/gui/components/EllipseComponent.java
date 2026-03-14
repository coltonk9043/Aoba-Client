/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.gui.Size;
import net.aoba.gui.colors.Color;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;

public class EllipseComponent extends Component {
	private Color color;

	public EllipseComponent() {
	}

	public EllipseComponent(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public Size measure(Size availableSize) {
		float w = getWidth() != null ? getWidth() : availableSize.getWidth();
		float h = getHeight() != null ? getHeight() : availableSize.getHeight();
		return new Size(w, h);
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		if (color != null) {
			float actualX = getActualSize().getX();
			float actualY = getActualSize().getY();
			float actualWidth = getActualSize().getWidth();
			float actualHeight = getActualSize().getHeight();

			float radiusX = actualWidth / 2f;
			float radiusY = actualHeight / 2f;
			float centerX = actualX + radiusX;
			float centerY = actualY + radiusY;

			Render2D.drawEllipse(drawContext, centerX, centerY, radiusX, radiusY, color);
		}

		super.draw(drawContext, partialTicks);
	}
}
