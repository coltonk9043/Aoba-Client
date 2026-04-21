/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.types.HorizontalAlignment;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.TextAlign;
import net.aoba.gui.types.Thickness;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class TabComponent extends PanelComponent {
	public static final UIProperty<Integer> SelectedIndexProperty = new UIProperty<>(
			"SelectedIndex", 0, false, false, TabComponent::onSelectedIndexChanged, TabComponent::coerceSelectedIndex);

	private static Integer coerceSelectedIndex(UIElement sender, Integer value) {
		if (sender instanceof TabComponent tc && value != null) {
			if (tc.items.isEmpty())
				return 0;
			return Math.max(0, Math.min(tc.items.size() - 1, value));
		}
		return value;
	}

	private final List<TabItemComponent> items = new ArrayList<>();
	private final RectangleComponent stripBackground;
	private final PanelComponent stripOverlay;
	private final RectangleComponent highlight;
	private final StackPanelComponent tabStrip;
	private Consumer<Integer> onSelectionChanged;

	public TabComponent() {
		stripBackground = new RectangleComponent();
		stripBackground.bindProperty(BackgroundProperty, GuiManager.windowBackgroundColor);
		stripBackground.bindProperty(BorderProperty, GuiManager.windowBorderColor);
		stripBackground.bindProperty(CornerRadiusProperty, GuiManager.roundingRadius);
		stripBackground.bindProperty(BorderThicknessProperty, GuiManager.lineThickness);

		stripOverlay = new PanelComponent();

		highlight = new RectangleComponent();
		highlight.bindProperty(BackgroundProperty, GuiManager.buttonBackgroundColor);
		highlight.setProperty(BorderProperty, null);
		highlight.setProperty(BorderThicknessProperty, 0f);
		highlight.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Stretch);
		highlight.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Stretch);
		highlight.setProperty(UIElement.IsHitTestVisibleProperty, false);
		stripOverlay.addChild(highlight);

		tabStrip = new StackPanelComponent();
		tabStrip.setDirection(StackPanelComponent.StackType.Horizontal);
		stripOverlay.addChild(tabStrip);

		stripBackground.setContent(stripOverlay);
		super.addChild(stripBackground);
	}

	@Override
	protected void onChildAdded(UIElement child) {
		if (child instanceof TabItemComponent item) {
			int idx = items.size();
			items.add(item);
			buildTabHeader(item, idx);
			item.setProperty(UIElement.IsVisibleProperty, idx == getSelectedIndex());
		}
	}

	@Override
	protected void onChildRemoved(UIElement child) {
		if (child instanceof TabItemComponent item) {
			items.remove(item);
			rebuildTabStrip();
		}
	}

	private void buildTabHeader(TabItemComponent item, int idx) {
		String header = item.getProperty(TabItemComponent.HeaderProperty);

		RectangleComponent cell = new RectangleComponent();
		cell.setProperty(UIElement.PaddingProperty, new Thickness(24f, 8f));
		cell.setProperty(UIElement.IsHitTestVisibleProperty, true);
		cell.setProperty(UIElement.CursorProperty, CursorStyle.Click);
		cell.setProperty(BackgroundProperty, null);
		cell.setProperty(BorderProperty, null);
		cell.setProperty(BorderThicknessProperty, 0f);

		StringComponent label = new StringComponent(header != null ? header : "");
		label.setProperty(StringComponent.TextAlignmentProperty, TextAlign.Center);
		label.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		label.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Stretch);
		label.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundColor);
		label.setProperty(StringComponent.FontSizeProperty, 14f);
		cell.setContent(label);

		cell.setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				setProperty(SelectedIndexProperty, idx);
				e.cancel();
			}
		});

		tabStrip.addChild(cell);
	}

	private void rebuildTabStrip() {
		tabStrip.clearChildren();
		for (int i = 0; i < items.size(); i++)
			buildTabHeader(items.get(i), i);
	}

	public int getSelectedIndex() {
		Integer idx = getProperty(SelectedIndexProperty);
		return idx != null ? idx : 0;
	}

	public void setOnSelectionChanged(Consumer<Integer> callback) {
		this.onSelectionChanged = callback;
	}

	private static void onSelectedIndexChanged(UIElement sender, Integer oldVal, Integer newVal) {
		if (sender instanceof TabComponent tc && newVal != null && newVal >= 0) {
			for (int i = 0; i < tc.items.size(); i++)
				tc.items.get(i).setProperty(UIElement.IsVisibleProperty, i == newVal);

			tc.syncHighlight();

			if (tc.onSelectionChanged != null)
				tc.onSelectionChanged.accept(newVal);
		}
	}

	@Override
	public void arrange(Rectangle finalSize) {
		super.arrange(finalSize);
		syncHighlight();
	}

	private void syncHighlight() {
		Integer idx = getProperty(SelectedIndexProperty);
		List<UIElement> cells = tabStrip.getChildren();
		if (idx == null || idx < 0 || idx >= cells.size())
			return;

		UIElement selectedCell = cells.get(idx);
		Rectangle cellRect = selectedCell.getActualSize();
		Rectangle stripRect = tabStrip.getActualSize();
		float left = cellRect.x() - stripRect.x();
		float right = stripRect.width() - left - cellRect.width();
		highlight.setProperty(UIElement.MarginProperty, new Thickness(left, 0f, right, 0f));
	}
}
