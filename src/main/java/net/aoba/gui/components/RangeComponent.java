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
import net.aoba.gui.types.GridDefinition.RelativeUnit;
import net.aoba.gui.types.HorizontalAlignment;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.TextWrapping;
import net.aoba.gui.types.Thickness;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.rendering.shaders.Shader;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.aoba.utils.types.Range;

public class RangeComponent extends Component {
	private enum DragTarget { NONE, LOW, HIGH }
	
	private static final float THUMB_CLICK_MARGIN = 6f;

	private DragTarget dragging = DragTarget.NONE;
	private float lastMouseX;

	public static UIProperty<String> HeaderProperty = new UIProperty<>("Header", "", false, true, RangeComponent::onHeaderPropertyChanged);
	public static UIProperty<Range> ValueProperty = new UIProperty<>("Value", new Range(0f, 0f), false, true,
			RangeComponent::onValuePropertyChanged, RangeComponent::coerceValue);
	public static UIProperty<Float> MinimumProperty = new UIProperty<>("Minimum", 0f, false, true);
	public static UIProperty<Float> MaximumProperty = new UIProperty<>("Maximum", 10f, false, true);
	public static UIProperty<Float> StepProperty = new UIProperty<>("Step", 0f, false, true);

	private final StringComponent headerComponent;
	private final StringComponent valueComponent;
	private final RectangleComponent fillBar;
	private final RectangleComponent trackBar;

	private Consumer<Range> onValueChanged;

	private static void onHeaderPropertyChanged(UIElement sender, String oldValue, String newValue) {
		if (sender instanceof RangeComponent range) {
			range.headerComponent.setProperty(StringComponent.TextProperty, newValue);
		}
	}

	private static Range coerceValue(UIElement sender, Range value) {
		if (sender instanceof RangeComponent range) {
			float min = range.getProperty(MinimumProperty);
			float max = range.getProperty(MaximumProperty);
			float step = range.getProperty(StepProperty);

			float lo = Math.max(min, Math.min(max, value.min()));
			float hi = Math.max(min, Math.min(max, value.max()));
			if (step > 0f) {
				lo = min + Math.round((lo - min) / step) * step;
				hi = min + Math.round((hi - min) / step) * step;
			}
			
			if (hi < lo) {
				hi = lo;
			}
			return new Range(lo, hi);
		}else
			return value;
	}

	private static void onValuePropertyChanged(UIElement sender, Range oldValue, Range newValue) {
		// Update the range text.
		if (sender instanceof RangeComponent range) {
			range.valueComponent.setProperty(StringComponent.TextProperty,
					String.format("%.02f - %.02f", newValue.min(), newValue.max()));
			range.updateFillBarComponent();
			if (range.onValueChanged != null)
				range.onValueChanged.accept(newValue);
		}
	}

	public RangeComponent() {
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

		Range initial = getProperty(ValueProperty);
		valueComponent = new StringComponent(String.format("%.02f - %.02f", initial.min(), initial.max()));
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

	@Override
	public void arrange(Rectangle finalSize) {
		super.arrange(finalSize);
		updateFillBarComponent();
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button != MouseButton.LEFT)
			return;

		if (event.action == MouseAction.DOWN) {
			if (getProperty(UIElement.IsHoveredProperty)) {
				float totalWidth = getActualSize().width();
				float actualX = getActualSize().x();
				if (totalWidth <= 0f) 
					return;

				Range value = getProperty(ValueProperty);
				float min = getProperty(MinimumProperty);
				float max = getProperty(MaximumProperty);
				
				float span = max - min;
				if (span <= 0f) 
					return;

				float lowPx = actualX + totalWidth * ((value.min() - min) / span);
				float highPx = actualX + totalWidth * ((value.max() - min) / span);

				if (Math.abs(highPx - lowPx) <= THUMB_CLICK_MARGIN) {
					float sharedPx = (lowPx + highPx) / 2f;
					dragging = lastMouseX >= sharedPx ? DragTarget.HIGH : DragTarget.LOW;
				} else {
					dragging = Math.abs(lastMouseX - lowPx) <= Math.abs(lastMouseX - highPx) ? DragTarget.LOW
							: DragTarget.HIGH;
				}
				updateValue(lastMouseX);
				event.cancel();
			}			
		} else if (event.action == MouseAction.UP) {
			dragging = DragTarget.NONE;
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		super.onMouseMove(event);
		
		// Update the value of the current dragged target.
		lastMouseX = (float) event.getX();
		if (Aoba.getInstance().guiManager.isClickGuiOpen() && dragging != DragTarget.NONE) {
			updateValue(lastMouseX);
		}
	}

	private void updateFillBarComponent() {
		if (getActualSize() == null)
			return;
		
		float totalWidth = getActualSize().width();
		if (totalWidth <= 0f)
			return;

		Range value = getProperty(ValueProperty);
		float min = getProperty(MinimumProperty);
		float max = getProperty(MaximumProperty);
		float range = max - min;
		if (range <= 0f) 
			return;

		float lowerValuePercentage = Math.max(0f, Math.min(1f, (value.min() - min) / range));
		float higherValuePercentage = Math.max(0f, Math.min(1f, (value.max() - min) / range));
		float lowerMargin = totalWidth * lowerValuePercentage;
		float higherMargin = totalWidth * higherValuePercentage;

		fillBar.setProperty(UIElement.MarginProperty, new Thickness(lowerMargin, 0f, 0f, 0f));
		fillBar.setProperty(UIElement.WidthProperty, Math.max(0f, higherMargin - lowerMargin));
	}
	
	private void updateValue(float mouseX) {
		float totalWidth = getActualSize().width();
		float actualX = getActualSize().x();
		
		if (totalWidth <= 0f) 
			return;

		float min = getProperty(MinimumProperty);
		float max = getProperty(MaximumProperty);
		float delta = Math.max(0f, Math.min(1f, (mouseX - actualX) / totalWidth));
		float newValue = min + delta * (max - min);

		Range current = getProperty(ValueProperty);
		setProperty(ValueProperty, dragging == DragTarget.LOW
				? new Range(Math.min(newValue, current.max()), current.max())
				: new Range(current.min(), Math.max(newValue, current.min())));
	}

	public void setOnValueChanged(Consumer<Range> onValueChanged) {
		this.onValueChanged = onValueChanged;
	}
}
