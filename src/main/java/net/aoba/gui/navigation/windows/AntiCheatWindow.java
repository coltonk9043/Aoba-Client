/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.windows;

import java.util.Arrays;
import net.aoba.Aoba;
import net.aoba.gui.components.ComboBoxComponent;
import net.aoba.gui.components.SeparatorComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.font.FontManager;
import net.aoba.gui.navigation.Window;
import net.aoba.gui.types.BindingMode;
import net.aoba.gui.types.SizeToContent;
import net.aoba.module.AntiCheat;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;

/**
 * Represents the AntiCheat Window that allows the user to select their
 * anticheat.
 */
public class AntiCheatWindow extends Window {
	public AntiCheatWindow() {
		super("AntiCheat", 50, 990);
		sizeToContent = SizeToContent.Both;
		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(8f);
		
		StringComponent headerText = new StringComponent("AntiCheat Settings");
		headerText.setProperty(UIElement.FontWeightProperty, FontManager.WEIGHT_BOLD);
		headerText.bindProperty(ForegroundProperty, GuiManager.foregroundHeaderColor);
		stackPanel.addChild(headerText);
	
		stackPanel.addChild(new SeparatorComponent());
		
		ComboBoxComponent comboBox = new ComboBoxComponent();
		comboBox.setProperty(ComboBoxComponent.ItemsSourceProperty, Arrays.asList(AntiCheat.values()));
		comboBox.bindProperty(ComboBoxComponent.SelectedItemProperty, Aoba.getInstance().moduleManager.antiCheat, BindingMode.TwoWay);
		stackPanel.addChild(comboBox);
		
		StringComponent detailText = new StringComponent("The selected AC will disable any features that are KNOWN detectable by that AC.");
		detailText.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundAccentColor);
		stackPanel.addChild(detailText);
		setContent(stackPanel);
		
		setProperty(UIElement.MinWidthProperty, 300f);
	}
}
