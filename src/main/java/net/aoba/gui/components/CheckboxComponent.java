/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.function.Consumer;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.colors.Color;
import net.aoba.gui.types.GridDefinition;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.gui.types.GridDefinition.RelativeUnit;
import net.aoba.rendering.shaders.Shader;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class CheckboxComponent extends Component {
	private static final Shader COLOR_ON = Shader.gradient(new Color(2, 212, 2), new Color(0, 154, 0, 200), 90);
	private static final Shader COLOR_OFF = Shader.gradient(new Color(189, 0, 0),new Color(154, 0, 0, 200), 90);		

	private Consumer<Boolean> onChanged;
	private final StringComponent headerComponent;
	private final RectangleComponent checkRectangle;

	public static final UIProperty<String> HeaderProperty  = new UIProperty<>("Header", "", false, true, CheckboxComponent::onHeaderChanged);
	public static final UIProperty<Boolean> IsCheckedProperty = new UIProperty<>("IsChecked", false, false, false, CheckboxComponent::onIsCheckedChanged);
	
	private static void onIsCheckedChanged(UIElement sender, Boolean oldValue, Boolean newValue) {
		if(sender instanceof CheckboxComponent checkbox) {
			checkbox.checkRectangle.setProperty(BackgroundProperty, newValue ? COLOR_ON : COLOR_OFF);
			
			if(checkbox.onChanged != null)
				checkbox.onChanged.accept(newValue);
		}
	}
	
	private static void onHeaderChanged(UIElement sender, String oldValue, String newValue) {
		if(sender instanceof CheckboxComponent checkbox) {
			checkbox.headerComponent.setProperty(StringComponent.TextProperty, newValue);
		}
	}
	
	public CheckboxComponent() {
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.setProperty(GridComponent.HorizontalSpacingProperty, 8f);
		
		checkRectangle = new RectangleComponent();
		checkRectangle.setProperty(BackgroundProperty,COLOR_OFF);
		checkRectangle.bindProperty(BorderProperty, GuiManager.componentBorderColor);
		checkRectangle.setProperty(CornerRadiusProperty, 3f);
		checkRectangle.setProperty(UIElement.CursorProperty, CursorStyle.Click);
		checkRectangle.setProperty(UIElement.WidthProperty, 20f);
		checkRectangle.setProperty(UIElement.HeightProperty, 20f);
		checkRectangle.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		grid.addChild(checkRectangle);
		
		headerComponent = new StringComponent("");
		headerComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		grid.addChild(headerComponent);

		setContent(grid);

		setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				toggle();
				e.cancel();
			}
		});
	}

	public void toggle() {
		Boolean isChecked = getProperty(IsCheckedProperty);
		setProperty(IsCheckedProperty, !Boolean.TRUE.equals(isChecked));
	}
	
	public void setOnChanged(Consumer<Boolean> onChanged) {
		this.onChanged = onChanged;
	}
}
