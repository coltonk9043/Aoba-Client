/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.gui.UIProperty;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;
import net.minecraft.resources.Identifier;

public class ImageComponent extends Component {
	public static final UIProperty<Identifier> ImageProperty = new UIProperty<>("Image", null, false, false);
	
	public ImageComponent() {
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		Identifier image = getProperty(ImageProperty);
		if (image != null) {
			float actualX = getActualSize().x();
			float actualY = getActualSize().y();
			float actualWidth = getActualSize().width();
			float actualHeight = getActualSize().height();

			Shader fgEffect = getProperty(ForegroundProperty);
			renderer.drawTexturedQuad(image, actualX, actualY, actualWidth, actualHeight, fgEffect);
		}
		super.draw(renderer, partialTicks);
	}
}
