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
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.colors.Color;
import net.aoba.gui.types.GridDefinition;
import net.aoba.gui.types.HorizontalAlignment;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.TextWrapping;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.gui.types.GridDefinition.RelativeUnit;
import net.aoba.rendering.shaders.Shader;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class SliderComponent extends Component {
	private boolean isSliding = false;
	private float currentSliderPosition = 0.4f;

	public static UIProperty<String> HeaderProperty = new UIProperty<>("Header", "", false, true, SliderComponent::onHeaderPropertyChanged);
	public static UIProperty<Float> ValueProperty = new UIProperty<>("Value", 0f, false, true,
			SliderComponent::onValuePropertyChanged, SliderComponent::coerceValue);
	public static UIProperty<Float> MinimumProperty = new UIProperty<>("Minimum", 0f, false, true);
	public static UIProperty<Float> MaximumProperty = new UIProperty<>("Maximum", 10f, false, true);
	public static UIProperty<Float> StepProperty = new UIProperty<>("Step", 0f, false, true);

	private final StringComponent headerComponent;
	private final StringComponent valueComponent;
	private final RectangleComponent fillBar;
	private final RectangleComponent trackBar;

	private Consumer<Float> onValueChanged;

	private static void onHeaderPropertyChanged(UIElement sender, String oldValue, String newValue) {
		if(sender instanceof SliderComponent slider) {
			slider.headerComponent.setProperty(StringComponent.TextProperty, newValue);
		}
	}
	
	private static Float coerceValue(UIElement sender, Float value) {
		if (!(sender instanceof SliderComponent slider) || value == null)
			return value;

		float min = slider.getProperty(MinimumProperty);
		float max = slider.getProperty(MaximumProperty);
		float coerced = Math.max(min, Math.min(max, value));
		float step = slider.getProperty(StepProperty);
		if (step > 0f)
			coerced = min + Math.round((coerced - min) / step) * step;
		return coerced;
	}
	
	private static void onValuePropertyChanged(UIElement sender, Float oldValue, Float newValue) {
		if(sender instanceof SliderComponent slider) {
			
			Float minValue = slider.getProperty(MinimumProperty);
			Float maxValue = slider.getProperty(MaximumProperty);
			float range = maxValue - minValue;
			slider.currentSliderPosition = range > 0f
					? Math.min(Math.max((newValue - minValue) / range, 0f), 1f)
					: 0f;
			slider.valueComponent.setProperty(StringComponent.TextProperty, String.format("%.02f", newValue));
			slider.updateFillWidth();
			
			if (slider.onValueChanged != null)
				slider.onValueChanged.accept(newValue);
		}
	}
	
	public SliderComponent() {
		currentSliderPosition = 0f;

		setProperty(UIElement.IsHitTestVisibleProperty, true);
		StackPanelComponent stack = new StackPanelComponent();
		stack.setSpacing(4f);
		
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));

		headerComponent = new StringComponent("");
		headerComponent.setProperty(StringComponent.TextProperty, getProperty(HeaderProperty));
		headerComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		grid.addChild(headerComponent);

		valueComponent = new StringComponent(String.format("%.02f", getProperty(ValueProperty)));
		valueComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		valueComponent.setProperty(StringComponent.TextWrappingProperty, TextWrapping.NoWrap);
		grid.addChild(valueComponent);
		stack.addChild(grid);

		PanelComponent barContainer = new PanelComponent();
		barContainer.setProperty(UIElement.HeightProperty, 14f);

		trackBar = new RectangleComponent();
		trackBar.setProperty(BackgroundProperty, Shader.solid(new Color(255, 255, 255, 100)));
		barContainer.addChild(trackBar);

		fillBar = new RectangleComponent();
		fillBar.bindProperty(BackgroundProperty, GuiManager.foregroundColor);
		fillBar.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Left);
		barContainer.addChild(fillBar);

		stack.addChild(barContainer);

		setContent(stack);
	}

	private void updateFillWidth() {
		if (getActualSize() == null)
			return;
		float totalWidth = getActualSize().width();
		fillBar.setProperty(UIElement.WidthProperty, totalWidth * currentSliderPosition);
	}

	@Override
	public void arrange(Rectangle finalSize) {
		super.arrange(finalSize);
		updateFillWidth();
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT) {
			if (event.action == MouseAction.DOWN) {
				if (getProperty(UIElement.IsHoveredProperty)) {
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

			float actualX = getActualSize().x();
			float actualWidth = getActualSize().width();

			float targetPosition = (float)  Math.max(0f,Math.min(((mouseX - actualX) / actualWidth), 1f));

			float minValue = getProperty(MinimumProperty);
			float maxValue = getProperty(MaximumProperty);
			setProperty(ValueProperty, (targetPosition * (maxValue - minValue)) + minValue);
		}
	}

	public void setOnValueChanged(Consumer<Float> onValueChanged) {
		this.onValueChanged = onValueChanged;
	}
}
