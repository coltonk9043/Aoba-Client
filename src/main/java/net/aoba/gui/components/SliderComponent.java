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
import net.aoba.gui.Rectangle;
import net.aoba.gui.GridDefinition;
import net.aoba.gui.GridDefinition.RelativeUnit;
import net.aoba.gui.GuiManager;
import net.aoba.gui.HorizontalAlignment;
import net.aoba.gui.TextWrapping;
import net.aoba.gui.Thickness;
import net.aoba.gui.VerticalAlignment;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.GuiGraphics;

public class SliderComponent extends Component {
	private float currentSliderPosition = 0.4f;

	private float minValue;
	private float maxValue;
	private float value;

	private boolean isSliding = false;

	private FloatSetting floatSetting;
	private Consumer<Float> onChanged;

	private final StringComponent valueComponent;
	private final EllipseComponent thumbEllipse;

	private SliderComponent(String headerText, float minValue, float maxValue, float value) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.value = value;
		currentSliderPosition = (value - minValue) / (maxValue - minValue);

		setHeight(45.0f);

		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));

		if (headerText != null) {
			StringComponent headerComponent = new StringComponent(headerText);
			headerComponent.setVerticalAlignment(VerticalAlignment.Center);
			grid.addChild(headerComponent);
		} else {
			grid.addChild(new Component() {});
		}

		valueComponent = new StringComponent(String.format("%.02f", value));
		valueComponent.setVerticalAlignment(VerticalAlignment.Center);
		valueComponent.setTextWrapping(TextWrapping.NoWrap);
		grid.addChild(valueComponent);

		addChild(grid);

		thumbEllipse = new EllipseComponent(GuiManager.foregroundColor.getValue());
		thumbEllipse.setWidth(12f);
		thumbEllipse.setHeight(12f);
		thumbEllipse.setHorizontalAlignment(HorizontalAlignment.Left);
		thumbEllipse.setMargin(new Thickness(0f, 29f, null, null));
		addChild(thumbEllipse);
	}

	public SliderComponent(float minValue, float maxValue, float value, Consumer<Float> onChanged) {
		this(null, minValue, maxValue, value);
		this.onChanged = onChanged;
	}

	public SliderComponent(FloatSetting floatSetting) {
		this(floatSetting.displayName, floatSetting.min_value, floatSetting.max_value, floatSetting.getValue());
		this.floatSetting = floatSetting;
		floatSetting.addOnUpdate(this::onSettingValueChanged);
	}

	private void onSettingValueChanged(Float f) {
		if (f != value) {
			value = f;
			currentSliderPosition = Math.min(Math.max((value - minValue) / (maxValue - minValue), 0f), 1f);
			valueComponent.setText(String.format("%.02f", value));
			updateThumbPosition();
		}
	}

	private void updateThumbPosition() {
		float actualWidth = getActualSize().getWidth();
		float leftOffset = actualWidth * currentSliderPosition - 6f;
		thumbEllipse.setMargin(new Thickness(leftOffset, 29f, null, null));
		thumbEllipse.setColor(GuiManager.foregroundColor.getValue());
	}

	@Override
	public void arrange(Rectangle finalSize) {
		super.arrange(finalSize);
		updateThumbPosition();
	}

	public float getSliderPosition() {
		return currentSliderPosition;
	}

	public void setSliderPosition(float pos) {
		currentSliderPosition = pos;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
		currentSliderPosition = Math.min(Math.max((value - minValue) / (maxValue - minValue), 0f), 1f);
		valueComponent.setText(String.format("%.02f", this.value));
		updateThumbPosition();
		if (floatSetting != null)
			floatSetting.setValue(value);
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
			valueComponent.setText(String.format("%.02f", value));
			updateThumbPosition();

			if (floatSetting != null)
				floatSetting.setValue(value);
			if (onChanged != null)
				onChanged.accept(value);
		}
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
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

		super.draw(drawContext, partialTicks);
	}
}
