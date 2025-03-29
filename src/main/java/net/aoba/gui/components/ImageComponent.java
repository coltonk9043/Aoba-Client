/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.gui.GuiManager;
import net.aoba.gui.Size;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class ImageComponent extends Component {

	public Identifier image;

	public ImageComponent() {
	}

	public ImageComponent(Identifier image) {
		this();
		this.image = image;
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(0f, 0f);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (image != null) {
			float actualX = getActualSize().getX();
			float actualY = getActualSize().getY();
			float actualWidth = getActualSize().getWidth();
			float actualHeight = getActualSize().getHeight();

			Render2D.drawTexturedQuad(drawContext, image, actualX, actualY, actualWidth, actualHeight,
					GuiManager.foregroundColor.getValue());
		}
		super.draw(drawContext, partialTicks);
	}
}
