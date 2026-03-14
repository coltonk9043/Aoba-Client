/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.gui.colors.Color;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;

public class RectangleComponent extends Component {
	private Color backgroundColor;
	private Color borderColor;
	private float cornerRadius = 0f;

	public RectangleComponent() {
	}

	public RectangleComponent(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public RectangleComponent(Color backgroundColor, Color borderColor, float cornerRadius) {
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.cornerRadius = cornerRadius;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public float getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(float cornerRadius) {
		this.cornerRadius = cornerRadius;
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		if (backgroundColor != null || borderColor != null) {
			float actualX = getActualSize().getX();
			float actualY = getActualSize().getY();
			float actualWidth = getActualSize().getWidth();
			float actualHeight = getActualSize().getHeight();

			if (borderColor != null && backgroundColor != null) {
				Render2D.drawOutlinedRoundedBox(drawContext, actualX, actualY, actualWidth, actualHeight,
						cornerRadius, borderColor, backgroundColor);
			} else if (backgroundColor != null) {
				Render2D.drawRoundedBox(drawContext, actualX, actualY, actualWidth, actualHeight,
						cornerRadius, backgroundColor);
			} else {
				Render2D.drawRoundedBoxOutline(drawContext, actualX, actualY, actualWidth, actualHeight,
						cornerRadius, borderColor);
			}
		}

		super.draw(drawContext, partialTicks);
	}
}
