/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.Size;
import net.aoba.gui.types.Thickness;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;

public class GraphComponent extends Component {
	public static final UIProperty<float[]> PointsProperty = new UIProperty<>("Points", null, false, false);
	public static final UIProperty<Thickness> PaddingProperty = new UIProperty<>("Padding", new Thickness(3f), false, true);
	public static final UIProperty<Float> LineThicknessProperty = new UIProperty<>("LineThickness", 1.25f, false, true);

	public GraphComponent() {
		
	}

	@Override
	public Size measure(Size availableSize) {
		Float width = getProperty(UIElement.WidthProperty);
		Float height = getProperty(UIElement.HeightProperty);
		float w = width != null ? width : availableSize.width();
		float h = height != null ? height : availableSize.height();
		return new Size(w, h);
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		float[] points = getProperty(PointsProperty);
		Shader lineShader = getProperty(UIElement.ForegroundProperty);
		Thickness padding = getProperty(GraphComponent.PaddingProperty);
		Float lineThickness = getProperty(GraphComponent.LineThicknessProperty);
		
		if (points != null && points.length >= 4 && lineShader != null) {
			Rectangle bounds = getActualSize();
			float left = bounds.x() + padding.left();
			float right = bounds.x() + bounds.width() - padding.right();
			float top = bounds.y() + padding.top();
			float bottom = bounds.y() + bounds.height() - padding.bottom();

			if (right > left && bottom > top) {
				float prevX = left + points[0] * (right - left);
				float prevY = bottom - points[1] * (bottom - top);
				for (int i = 2; i < points.length; i += 2) {
					float x = left + points[i] * (right - left);
					float y = bottom - points[i + 1] * (bottom - top);
					renderer.drawLine(prevX, prevY, x, y, lineThickness, lineShader);
					prevX = x;
					prevY = y;
				}
			}
		}

		super.draw(renderer, partialTicks);
	}
}
