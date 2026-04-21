/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.aoba.Aoba;
import net.aoba.rendering.utils.PolygonBank;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.navigation.Popup;
import net.aoba.gui.types.GridDefinition;
import net.aoba.gui.types.Thickness;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.gui.types.GridDefinition.RelativeUnit;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class ComboBoxComponent extends Component {
	private final StringComponent selectedLabel;

	public static UIProperty<List<?>> ItemsSourceProperty = new UIProperty<>("ItemsSource", List.of());
	public static UIProperty<Function<Object, Component>> ItemsTemplate = new UIProperty<>("ItemsTemplate", null);
	public static UIProperty<Object> SelectedItemProperty = new UIProperty<>("SelectedItem", null, false, true, ComboBoxComponent::onSelectedItemPropertyChanged);
	public static UIProperty<String> PlaceholderTextProperty = new UIProperty<>("PlaceholderText", null);
	
	private Consumer<Object> onChanged;

	public ComboBoxComponent() {
		this.setProperty(UIElement.CursorProperty, CursorStyle.Click);
		
		selectedLabel = new StringComponent("");
		selectedLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);

		PolygonComponent arrow = new PolygonComponent(PolygonBank.ARROW_DOWN);
		arrow.setProperty(UIElement.WidthProperty, 16f);
		arrow.setProperty(UIElement.HeightProperty, 16f);
		arrow.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);

		GridComponent layout = new GridComponent();
		layout.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		layout.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));
		layout.setProperty(GridComponent.HorizontalSpacingProperty, 6f);
		layout.addChild(selectedLabel);
		layout.addChild(arrow);

		RectangleComponent box = new RectangleComponent();
		box.bindProperty(BackgroundProperty, GuiManager.componentBackgroundColor);
		box.bindProperty(BorderProperty, GuiManager.componentBorderColor);
		box.bindProperty(CornerRadiusProperty, GuiManager.roundingRadius);
		box.setProperty(UIElement.PaddingProperty, new Thickness(8f));
		box.setProperty(UIElement.IsHitTestVisibleProperty, true);
		box.setContent(layout);

		box.setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				openDropdown();
				e.cancel();
			}
		});
		setContent(box);
	}

	private static void onSelectedItemPropertyChanged(UIElement sender, Object oldValue, Object newValue) {
		if(sender instanceof ComboBoxComponent comboBox) {
			comboBox.updateSelectedLabel();
			if(comboBox.onChanged != null)
				comboBox.onChanged.accept(newValue);
		}
	}

	private void openDropdown() {
		StackPanelComponent itemList = new StackPanelComponent();
		itemList.setSpacing(4f);

		List<?> items = getProperty(ComboBoxComponent.ItemsSourceProperty);
		Function<Object, Component> templateFactory = getProperty(ComboBoxComponent.ItemsTemplate);

		for (Object item : items) {
			Component row;
			if (templateFactory != null) {
				row = templateFactory.apply(item);
			} else {
				row = new StringComponent(item != null ? item.toString() : "");
			}

			row.setProperty(UIElement.IsHitTestVisibleProperty, true);
			row.setProperty(UIElement.MarginProperty, new Thickness(4f));
			row.setOnClicked(e -> {
				if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
					setProperty(ComboBoxComponent.SelectedItemProperty, item);
					Aoba.getInstance().guiManager.closePopup();
					e.cancel();
				}
			});
			itemList.addChild(row);
		}

		ScrollComponent scroll = new ScrollComponent();
		scroll.setProperty(UIElement.MaxHeightProperty, 200f);
		scroll.setContent(itemList);

		Aoba.getInstance().guiManager.openPopup(this, scroll, Popup.PlacementMode.Bottom);
	}

	private void updateSelectedLabel() {
		Object value = getProperty(ComboBoxComponent.SelectedItemProperty);
		String placeholderText = getProperty(ComboBoxComponent.PlaceholderTextProperty);
		if (value != null)
			selectedLabel.setProperty(StringComponent.TextProperty, value.toString());
		else if (placeholderText != null)
			selectedLabel.setProperty(StringComponent.TextProperty, placeholderText);
		else
			selectedLabel.setProperty(StringComponent.TextProperty, "");
	}

	public void setOnItemChanged(Consumer<Object> onChanged) {
		this.onChanged = onChanged;
	}
}
