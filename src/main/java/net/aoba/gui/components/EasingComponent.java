/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.EnumMap;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.colors.Color;
import net.aoba.gui.types.HorizontalAlignment;
import net.aoba.gui.types.TextAlign;
import net.aoba.gui.types.TextWrapping;
import net.aoba.gui.types.Thickness;
import net.aoba.managers.rotation.goals.EasingFunction;
import net.aoba.rendering.shaders.Shader;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class EasingComponent extends Component {
	private static final float CELL_WIDTH = 120f;
	private static final float CELL_HEIGHT = 84f;
	private static final float GRAPH_HEIGHT = 56f;
	private static final float LIST_HEIGHT = 240f;

	// Cache the graphs so that the easing graphs can be reused
	private static final int SAMPLES = 48;
	private static final EnumMap<EasingFunction, float[]> CURVE_CACHE = buildCurveCache();

	private static final Shader SELECTED_EFFECT = Shader.gradient(new Color(2, 212, 2), new Color(0, 154, 0, 200), 90);

	public static UIProperty<EasingFunction> SelectedValueProperty = new UIProperty<>("SelectedValue",
			EasingFunction.SineEaseInOut, false, false, EasingComponent::OnSelectedValueChanged);

	private final EnumMap<EasingFunction, RectangleComponent> cellByFunction = new EnumMap<>(EasingFunction.class);

	private static void OnSelectedValueChanged(UIElement sender, EasingFunction oldValue, EasingFunction newValue) {
		if (sender instanceof EasingComponent easing) {
			easing.refreshFunctionComponents();
		}
	}

	public EasingComponent() {
		ScrollComponent scroll = new ScrollComponent();
		scroll.setProperty(UIElement.HeightProperty, LIST_HEIGHT);
		WrapPanelComponent wrapPanel = new WrapPanelComponent();
		wrapPanel.setVirtualized(true);
		wrapPanel.setProperty(WrapPanelComponent.ItemSpacingProperty, 4f);
		wrapPanel.setProperty(WrapPanelComponent.RowSpacingProperty, 4f);
		scroll.setContent(wrapPanel);
		setContent(scroll);

		for (EasingFunction function : EasingFunction.values()) {
			RectangleComponent cell = new RectangleComponent();
			cell.setProperty(UIElement.WidthProperty, CELL_WIDTH);
			cell.setProperty(UIElement.HeightProperty, CELL_HEIGHT);
			cell.setProperty(UIElement.PaddingProperty, new Thickness(4f));
			cell.bindProperty(UIElement.BorderProperty, GuiManager.componentBorderColor);
			cell.bindProperty(UIElement.BackgroundProperty, GuiManager.componentBackgroundColor);
			cell.setProperty(UIElement.ToolTipProperty, function.name());

			StackPanelComponent inner = new StackPanelComponent();
			inner.setSpacing(2f);

			GraphComponent graph = new GraphComponent();
			graph.setProperty(GraphComponent.PointsProperty, CURVE_CACHE.get(function));
			graph.setProperty(UIElement.HeightProperty, GRAPH_HEIGHT);
			graph.setProperty(UIElement.IsHitTestVisibleProperty, false);
			graph.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundHeaderColor);
			inner.addChild(graph);

			StringComponent label = new StringComponent(function.name());
			label.setProperty(StringComponent.TextAlignmentProperty, TextAlign.Center);
			label.setProperty(StringComponent.TextWrappingProperty, TextWrapping.NoWrap);
			label.setProperty(StringComponent.FontSizeProperty, 9f);
			label.setProperty(UIElement.IsHitTestVisibleProperty, false);
			inner.addChild(label);

			cell.setContent(inner);

			cell.setOnClicked(e -> {
				if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
					setProperty(SelectedValueProperty, function);
					refreshFunctionComponents();
					e.cancel();
				}
			});

			cellByFunction.put(function, cell);
			wrapPanel.addChild(cell);
		}

		refreshFunctionComponents();
	}

	private static EnumMap<EasingFunction, float[]> buildCurveCache() {
		EnumMap<EasingFunction, float[]> cache = new EnumMap<>(EasingFunction.class);
		for (EasingFunction function : EasingFunction.values()) {
			double[] values = new double[SAMPLES];
			
			// Calculate MIX and MAX.
			double min = Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;
			for (int i = 0; i < SAMPLES; i++) {
				double t = i / (double) (SAMPLES - 1);
				double value = EasingFunction.ease(function, t);
				values[i] = value;
				min = Math.min(min, value);
				max = Math.max(max, value);
			}

			// Calculate the range and generate points.
			double range = max - min;
			float[] points = new float[SAMPLES * 2];
			for (int i = 0; i < SAMPLES; i++) {
				points[i * 2] = (float) (i / (double) (SAMPLES - 1));
				points[i * 2 + 1] = (float) ((values[i] - min) / range);
			}
			cache.put(function, points);
		}
		return cache;
	}

	private void refreshFunctionComponents() {
		EasingFunction selected = getProperty(SelectedValueProperty);
		for (var entry : cellByFunction.entrySet()) {
			RectangleComponent cell = entry.getValue();
			if (entry.getKey() == selected) {
				cell.unbindProperty(UIElement.BackgroundProperty);
				cell.setProperty(UIElement.BackgroundProperty, SELECTED_EFFECT);
			} else {
				cell.bindProperty(UIElement.BackgroundProperty, GuiManager.componentBackgroundColor);
			}
		}
	}
}
