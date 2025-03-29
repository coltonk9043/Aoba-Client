/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Size;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

public class SliderComponent extends Component {
	private float currentSliderPosition = 0.4f;

	private float minValue;
	private float maxValue;
	private float value;

	private boolean isSliding = false;

	FloatSetting floatSetting;

	public SliderComponent() {
		floatSetting = null;
		setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	public SliderComponent(FloatSetting floatSetting) {
		this.floatSetting = floatSetting;
		minValue = floatSetting.min_value;
		maxValue = floatSetting.max_value;
		header = floatSetting.displayName;
		value = floatSetting.getValue();
		currentSliderPosition = (value - minValue) / (maxValue - minValue);

		floatSetting.addOnUpdate(f -> {
			value = f;
			currentSliderPosition = Math.min(Math.max((value - minValue) / (maxValue - minValue), 0f), 1f);
		});

		setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 45.0f);
	}

	public float getSliderPosition() {
		return currentSliderPosition;
	}

	public void setSliderPosition(float pos) {
		currentSliderPosition = pos;
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT) {
			if (event.action == MouseAction.DOWN) {
				if (hovered) {
					isSliding = true;
					event.cancel();
				}
			} else if (event.action == MouseAction.UP) {
				isSliding = false;
			}
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		super.onMouseMove(event);

		if (Aoba.getInstance().guiManager.isClickGuiOpen() && isSliding) {
			double mouseX = event.getX();

			float actualX = getActualSize().getX();
			float actualWidth = getActualSize().getWidth();

			float targetPosition = (float) Math.min(((mouseX - actualX) / actualWidth), 1f);
			targetPosition = Math.max(0f, targetPosition);

			currentSliderPosition = targetPosition;
			value = (currentSliderPosition * (maxValue - minValue)) + minValue;

			if (floatSetting != null)
				floatSetting.setValue(value);
		}
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (floatSetting == null) {
			return;
		}

		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();

		float filledLength = actualWidth * currentSliderPosition;

		if (floatSetting != null) {
			float defaultLength = actualWidth * ((floatSetting.getDefaultValue() - minValue) / (maxValue - minValue));
			Render2D.drawBox(drawContext, actualX + defaultLength - 2, actualY + 28, 4, 14, Colors.White);
		}

		Render2D.drawBox(drawContext, actualX, actualY + 34, filledLength, 2, GuiManager.foregroundColor.getValue());
		Render2D.drawBox(drawContext, actualX + filledLength, actualY + 34, (actualWidth - filledLength), 2,
				new Color(255, 255, 255, 255));

		Render2D.drawCircle(drawContext, actualX + filledLength, actualY + 35, 6,
				GuiManager.foregroundColor.getValue());

		if (header != null) {
			Render2D.drawString(drawContext, header, actualX, actualY + 8, 0xFFFFFF);
		}

		String valueText = String.format("%.02f", value);
		int textSize = MC.textRenderer.getWidth(valueText) * 2;
		Render2D.drawString(drawContext, valueText, actualX + actualWidth - 6 - textSize, actualY + 8, 0xFFFFFF);

		super.draw(drawContext, partialTicks);
	}
}
