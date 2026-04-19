/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.function.Consumer;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.types.Size;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class ColorPickerComponent extends Component {

	private boolean isSliding = false;
	private int slidingPart = -1; // 0=SL, 1=hue, 2=alpha
	private Consumer<Color> onChanged;

	public static final UIProperty<Color> ColorProperty = new UIProperty<>("Color", Colors.White, false, false, ColorPickerComponent::onColorPropertyChanged);
	
	private static void onColorPropertyChanged(UIElement sender, Color oldValue, Color newValue) {
		if(sender instanceof ColorPickerComponent colorPicker) {
			if (colorPicker.onChanged != null)
				colorPicker.onChanged.accept(newValue);
		}
	}
	
	public ColorPickerComponent() {
	}
	
	public void setOnChanged(Consumer<Color> callback) {
		this.onChanged = callback;
	}
	
	@Override
	public Size measure(Size availableSize) {
		return new Size(availableSize.width(), 116.0f);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT) {
			boolean hovered = getProperty(UIElement.IsHoveredProperty);
			if (event.action == MouseAction.DOWN && hovered) {
				float mouseX = (float) event.mouseX;
				float actualX = getActualSize().x();
				float actualWidth = getActualSize().width();

				if (mouseX >= actualX && mouseX <= actualX + actualWidth - 76)
					slidingPart = 0;
				else if (mouseX >= actualX + actualWidth - 68 && mouseX <= actualX + actualWidth - 38)
					slidingPart = 1;
				else if (mouseX >= actualX + actualWidth - 30 && mouseX <= actualX + actualWidth)
					slidingPart = 2;

				if (slidingPart >= 0) {
					isSliding = true;
					event.cancel();
				}
			} else if (event.action == MouseAction.UP) {
				isSliding = false;
				slidingPart = -1;
			}
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		super.onMouseMove(event);

		
		if (!Aoba.getInstance().guiManager.isClickGuiOpen() || !isSliding)
			return;

		Color curColor = getProperty(ColorProperty);

		float actualX = getActualSize().x();
		float actualY = getActualSize().y();
		float actualWidth = getActualSize().width();
		float actualHeight = getActualSize().height();

		double mouseX = event.getX();
		double mouseY = event.getY();
		float vertical = (float) Math.min(Math.max(1.0f - ((mouseY - actualY - 1) / actualHeight), 0), 1);

		Color newColor;
		if (slidingPart == 0) {
			float horizontal = (float) Math.min(Math.max(((mouseX - actualX) - 1) / (actualWidth - 76), 0), 1);
			newColor = curColor.withHSV(curColor.getHue(), horizontal, vertical);
		} else if (slidingPart == 1) {
			newColor = curColor.withHue((1.0f - vertical) * 360.0f);
		} else if (slidingPart == 2) {
			newColor = curColor.withAlpha(Math.round(vertical * 255.0f));
		} else {
			return;
		}

		setProperty(ColorProperty, newColor);
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		float actualX = getActualSize().x();
		float actualY = getActualSize().y();
		float actualWidth = getActualSize().width();
		float actualHeight = getActualSize().height();

		// Saturation/Luminance gradient
		Color color = getProperty(ColorProperty);
		Color hueColor = new Color(color.getHue(), 1.0f, 1.0f);
		renderer.drawRoundedBox(actualX, actualY, actualWidth - 76, actualHeight, 0f,
				Shader.gradient(Colors.White, hueColor, 0f));
		renderer.drawRoundedBox(actualX, actualY, actualWidth - 76, actualHeight, 0f,
				Shader.gradient(new Color(0, 0, 0, 0), Colors.Black, 90f));

		// Hue bar 
		float inc = actualHeight / 6.0f;
		renderer.drawRoundedBox(actualX + actualWidth - 68, actualY, 30, inc, 0f,
				Shader.gradient(Colors.Red, Colors.Yellow, 90f));
		renderer.drawRoundedBox(actualX + actualWidth - 68, actualY + inc, 30, inc, 0f,
				Shader.gradient(Colors.Yellow, Colors.Green, 90f));
		renderer.drawRoundedBox(actualX + actualWidth - 68, actualY + 2 * inc, 30, inc, 0f,
				Shader.gradient(Colors.Green, Colors.Cyan, 90f));
		renderer.drawRoundedBox(actualX + actualWidth - 68, actualY + 3 * inc, 30, inc, 0f,
				Shader.gradient(Colors.Cyan, Colors.Blue, 90f));
		renderer.drawRoundedBox(actualX + actualWidth - 68, actualY + 4 * inc, 30, inc, 0f,
				Shader.gradient(Colors.Blue, Colors.Purple, 90f));
		renderer.drawRoundedBox(actualX + actualWidth - 68, actualY + 5 * inc, 30, inc, 0f,
				Shader.gradient(Colors.Purple, Colors.Red, 90f));

		// Alpha bar
		renderer.drawRoundedBox(actualX + actualWidth - 30, actualY, 30, actualHeight, 0f,
				Shader.gradient(Colors.White, Colors.Black, 90f));

		// Outlines
		Shader blackOutline = Shader.solid(Colors.Black);
		renderer.drawBoxOutline(actualX, actualY, actualWidth - 76, actualHeight, blackOutline);
		renderer.drawBoxOutline(actualX + actualWidth - 68, actualY, 30, actualHeight, blackOutline);
		renderer.drawBoxOutline(actualX + actualWidth - 30, actualY, 30, actualHeight, blackOutline);

		// Indicators
		Shader whiteFill = Shader.solid(Colors.White);
		renderer.drawEllipse(actualX + (color.getSaturation() * (actualWidth - 76)),
				actualY + ((1.0f - color.getLuminance()) * actualHeight), 3, whiteFill);
		renderer.drawOutlinedBox(actualX + actualWidth - 68, actualY + ((color.getHue() / 360.0f) * actualHeight), 30,
				3, blackOutline, whiteFill);
		renderer.drawOutlinedBox(actualX + actualWidth - 30,
				actualY + (((255.0f - (color.getAlpha() * 255)) / 255.0f) * actualHeight), 30, 3, blackOutline,
				whiteFill);
	}
}
