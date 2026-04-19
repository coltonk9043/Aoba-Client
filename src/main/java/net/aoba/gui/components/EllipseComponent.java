/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.gui.UIElement;
import net.aoba.gui.types.Size;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;

public class EllipseComponent extends Component {
	public EllipseComponent() {
	}

	@Override
	public Size measure(Size availableSize) {
		Float width = getProperty(UIElement.WidthProperty);
		Float height = getProperty(UIElement.HeightProperty);
		float w = width != null ? width : availableSize.width();
		float h = height != null ? height : availableSize.height();
		return new Size(w, h);
	}
	
	public void draw(Renderer2D renderer, float partialTicks) {
		float actualX = getActualSize().x();
		float actualY = getActualSize().y();
		float actualWidth = getActualSize().width();
		float actualHeight = getActualSize().height();

		float radiusX = actualWidth / 2f;
		float radiusY = actualHeight / 2f;
		float centerX = actualX + radiusX;
		float centerY = actualY + radiusY;

		Shader bgEffect = getProperty(UIElement.BackgroundProperty);
		if (bgEffect != null) {
			renderer.drawEllipse(centerX, centerY, radiusX, radiusY, bgEffect);
		}

		super.draw(renderer, partialTicks);
	}
}
