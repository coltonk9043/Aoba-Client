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
import net.aoba.gui.UIProperty;
import net.aoba.gui.types.GridDefinition;
import net.aoba.gui.types.Thickness;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.gui.types.GridDefinition.RelativeUnit;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class ExpanderComponent extends Component {
	private final StackPanelComponent layout;
	private final StringComponent toggleLabel;

	public static UIProperty<Boolean> IsExpandedProperty = new UIProperty<>("IsExpanded", false, false, true, ExpanderComponent::onIsExpandedPropertyChanged);
	public static UIProperty<UIElement> ExpanderContentProperty = new UIProperty<>("ExpanderContent", null, false, true, ExpanderComponent::onExpanderContentPropertyChanged);
	
	private static void onIsExpandedPropertyChanged(UIElement sender, Boolean oldValue, Boolean newValue) {
		if(sender instanceof ExpanderComponent expander) {
			expander.toggleLabel.setProperty(StringComponent.TextProperty, newValue ? "<<" : ">>");
			
			UIElement expanderContent = expander.getProperty(ExpanderContentProperty);
			if (expanderContent != null) {
				expanderContent.setProperty(UIElement.IsVisibleProperty, newValue);
			}
		}
	}
	
	private static void onExpanderContentPropertyChanged(UIElement sender, UIElement oldValue, UIElement newValue) {
		if(sender instanceof ExpanderComponent expander) {
			if (oldValue != null) {
				expander.layout.removeChild(oldValue);
			}

			if (newValue != null) {
				boolean isExpanded = expander.getProperty(IsExpandedProperty);
				newValue.setProperty(UIElement.IsVisibleProperty, isExpanded);
				expander.layout.addChild(newValue);
			}
		}
	}
	
	public ExpanderComponent(String headerText) {
		RectangleComponent container = new RectangleComponent();
		container.bindProperty(UIElement.BackgroundProperty, GuiManager.panelBackgroundColor);
		container.setProperty(UIElement.CornerRadiusProperty, 6f);
		container.setProperty(UIElement.PaddingProperty, new Thickness(8f));
		
		layout = new StackPanelComponent();
		layout.setSpacing(4f);
		
		GridComponent headerGrid = new GridComponent();
		headerGrid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		headerGrid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));
		headerGrid.setProperty(UIElement.CursorProperty, CursorStyle.Click);
		
		StringComponent headerLabel = new StringComponent(headerText);
		headerLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		headerGrid.addChild(headerLabel);

		toggleLabel = new StringComponent(">>");
		toggleLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		toggleLabel.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundColor);
		headerGrid.addChild(toggleLabel);

		headerGrid.setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				boolean isExpanded = getProperty(IsExpandedProperty);
				setProperty(IsExpandedProperty, !isExpanded);
				e.cancel();
			}
		});

		layout.addChild(headerGrid);
		container.setContent(layout);
		super.setContent(container);
	}

	@Override
	public void setContent(UIElement content) {
		setProperty(ExpanderContentProperty, content);
	}
}
