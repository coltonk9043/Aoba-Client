/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.rendering.Renderer2D;

public class SeparatorComponent extends Component {

	public SeparatorComponent() {
		setProperty(UIElement.HeightProperty, 1.0f);
		bindProperty(BorderProperty, GuiManager.componentBorderColor);
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		float actualX = getActualSize().x();
		float actualY = getActualSize().y();
		float actualWidth = getActualSize().width();
		float actualHeight = getActualSize().height();

		renderer.drawLine(actualX, actualY, actualX + actualWidth, actualY + actualHeight, getProperty(BorderProperty));
	}
}
