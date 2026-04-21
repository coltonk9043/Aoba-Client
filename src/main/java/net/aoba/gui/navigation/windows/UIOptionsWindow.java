/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.windows;

import java.util.List;
import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.components.CheckboxComponent;
import net.aoba.gui.components.ExpanderComponent;
import net.aoba.gui.components.ShaderComponent;
import net.aoba.gui.components.KeybindComponent;
import net.aoba.gui.components.ComboBoxComponent;
import net.aoba.gui.components.ScrollComponent;
import net.aoba.gui.components.SliderComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.font.FontManager;
import net.aoba.gui.navigation.Window;
import net.aoba.gui.types.BindingMode;
import net.aoba.gui.types.SizeToContent;

public class UIOptionsWindow extends Window {
	public UIOptionsWindow() {
		super("UI Settings", 600, 200);
		sizeToContent = SizeToContent.Both;
		setProperty(UIElement.MinWidthProperty, 400.0f);

		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(8f);

		// Keybinds
		StringComponent keybindsHeader = new StringComponent("Keybinds");
		keybindsHeader.setProperty(UIElement.FontWeightProperty, FontManager.WEIGHT_BOLD);
		keybindsHeader.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundHeaderColor);
		stackPanel.addChild(keybindsHeader);
		
		KeybindComponent clickGuiButtonComponent = new KeybindComponent();
		clickGuiButtonComponent.bindProperty(KeybindComponent.SelectedKeyProperty, Aoba.getInstance().guiManager.clickGuiButton, BindingMode.TwoWay);
		clickGuiButtonComponent.setProperty(KeybindComponent.HeaderProperty, Aoba.getInstance().guiManager.clickGuiButton.displayName);
		stackPanel.addChild(clickGuiButtonComponent);

		// Font
		StringComponent fontHeader = new StringComponent("HUD Font");
		fontHeader.setProperty(UIElement.FontWeightProperty, FontManager.WEIGHT_BOLD);
		fontHeader.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundHeaderColor);
		stackPanel.addChild(fontHeader);
		List<String> fontNames = Aoba.getInstance().fontManager.fonts.keySet().stream().toList();
		ComboBoxComponent fontCombo = new ComboBoxComponent();
		fontCombo.setProperty(ComboBoxComponent.ItemsSourceProperty, fontNames);
		fontCombo.setProperty(ComboBoxComponent.ItemsTemplate, name -> {
			StringComponent label = new StringComponent((String) name);
			label.setProperty(UIElement.FontProperty, Aoba.getInstance().fontManager.getFont((String) name));
			return label;
		});
		fontCombo.setOnItemChanged(s -> {
			if(s instanceof String name)
				GuiManager.fontSetting.setFontName(name);
		});
		fontCombo.setProperty(ComboBoxComponent.SelectedItemProperty, GuiManager.fontSetting.getFontName());
		stackPanel.addChild(fontCombo);

		// Colors
		StringComponent colorsHeader = new StringComponent("Colors");
		colorsHeader.setProperty(UIElement.FontWeightProperty, FontManager.WEIGHT_BOLD);
		colorsHeader.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundHeaderColor);
		stackPanel.addChild(colorsHeader);

		StackPanelComponent colorStack = new StackPanelComponent();
		colorStack.setSpacing(8f);

		ShaderComponent foregroundShader = new ShaderComponent();
		foregroundShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.foregroundColor, BindingMode.TwoWay);
		ExpanderComponent foregroundExpander = new ExpanderComponent(GuiManager.foregroundColor.displayName);
		foregroundExpander.setProperty(ExpanderComponent.ExpanderContentProperty, foregroundShader);
		colorStack.addChild(foregroundExpander);

		ShaderComponent foregroundHeaderShader = new ShaderComponent();
		foregroundHeaderShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.foregroundHeaderColor, BindingMode.TwoWay);
		ExpanderComponent foregroundHeaderExpander = new ExpanderComponent(GuiManager.foregroundHeaderColor.displayName);
		foregroundHeaderExpander.setProperty(ExpanderComponent.ExpanderContentProperty, foregroundHeaderShader);
		colorStack.addChild(foregroundHeaderExpander);

		ShaderComponent foregroundAccentShader = new ShaderComponent();
		foregroundAccentShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.foregroundAccentColor, BindingMode.TwoWay);
		ExpanderComponent foregroundAccentExpander = new ExpanderComponent(GuiManager.foregroundAccentColor.displayName);
		foregroundAccentExpander.setProperty(ExpanderComponent.ExpanderContentProperty, foregroundAccentShader);
		colorStack.addChild(foregroundAccentExpander);

		ShaderComponent windowBorderShader = new ShaderComponent();
		windowBorderShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.windowBorderColor, BindingMode.TwoWay);
		ExpanderComponent windowBorderExpander = new ExpanderComponent(GuiManager.windowBorderColor.displayName);
		windowBorderExpander.setProperty(ExpanderComponent.ExpanderContentProperty, windowBorderShader);
		colorStack.addChild(windowBorderExpander);

		ShaderComponent panelBorderShader = new ShaderComponent();
		panelBorderShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.panelBorderColor, BindingMode.TwoWay);
		ExpanderComponent panelBorderExpander = new ExpanderComponent(GuiManager.panelBorderColor.displayName);
		panelBorderExpander.setProperty(ExpanderComponent.ExpanderContentProperty, panelBorderShader);
		colorStack.addChild(panelBorderExpander);

		ShaderComponent componentBorderShader = new ShaderComponent();
		componentBorderShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.componentBorderColor, BindingMode.TwoWay);
		ExpanderComponent componentBorderExpander = new ExpanderComponent(GuiManager.componentBorderColor.displayName);
		componentBorderExpander.setProperty(ExpanderComponent.ExpanderContentProperty, componentBorderShader);
		colorStack.addChild(componentBorderExpander);

		ShaderComponent windowBgShader = new ShaderComponent();
		windowBgShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.windowBackgroundColor, BindingMode.TwoWay);
		ExpanderComponent windowBgExpander = new ExpanderComponent(GuiManager.windowBackgroundColor.displayName);
		windowBgExpander.setProperty(ExpanderComponent.ExpanderContentProperty, windowBgShader);
		colorStack.addChild(windowBgExpander);

		ShaderComponent panelBgShader = new ShaderComponent();
		panelBgShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.panelBackgroundColor, BindingMode.TwoWay);
		ExpanderComponent panelBgExpander = new ExpanderComponent(GuiManager.panelBackgroundColor.displayName);
		panelBgExpander.setProperty(ExpanderComponent.ExpanderContentProperty, panelBgShader);
		colorStack.addChild(panelBgExpander);

		ShaderComponent componentBgShader = new ShaderComponent();
		componentBgShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.componentBackgroundColor, BindingMode.TwoWay);
		ExpanderComponent componentBgExpander = new ExpanderComponent(GuiManager.componentBackgroundColor.displayName);
		componentBgExpander.setProperty(ExpanderComponent.ExpanderContentProperty, componentBgShader);
		colorStack.addChild(componentBgExpander);

		ShaderComponent buttonBgShader = new ShaderComponent();
		buttonBgShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.buttonBackgroundColor, BindingMode.TwoWay);
		ExpanderComponent buttonBgExpander = new ExpanderComponent(GuiManager.buttonBackgroundColor.displayName);
		buttonBgExpander.setProperty(ExpanderComponent.ExpanderContentProperty, buttonBgShader);
		colorStack.addChild(buttonBgExpander);

		ShaderComponent buttonHoverBgShader = new ShaderComponent();
		buttonHoverBgShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.buttonHoverBackgroundColor, BindingMode.TwoWay);
		ExpanderComponent buttonHoverBgExpander = new ExpanderComponent(GuiManager.buttonHoverBackgroundColor.displayName);
		buttonHoverBgExpander.setProperty(ExpanderComponent.ExpanderContentProperty, buttonHoverBgShader);
		colorStack.addChild(buttonHoverBgExpander);

		ShaderComponent buttonBorderShader = new ShaderComponent();
		buttonBorderShader.bindProperty(ShaderComponent.ShaderProperty, GuiManager.buttonBorderColor, BindingMode.TwoWay);
		ExpanderComponent buttonBorderExpander = new ExpanderComponent(GuiManager.buttonBorderColor.displayName);
		buttonBorderExpander.setProperty(ExpanderComponent.ExpanderContentProperty, buttonBorderShader);
		colorStack.addChild(buttonBorderExpander);

		ScrollComponent colorScroll = new ScrollComponent();
		colorScroll.setContent(colorStack);
		colorScroll.setProperty(UIElement.ClipToBoundsProperty, true);
		colorScroll.setProperty(UIElement.MaxHeightProperty, 300.0f);
		stackPanel.addChild(colorScroll);

		// Styling
		StringComponent stylingHeader = new StringComponent("Styling");
		stylingHeader.setProperty(UIElement.FontWeightProperty, FontManager.WEIGHT_BOLD);
		stylingHeader.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundHeaderColor);
		stackPanel.addChild(stylingHeader);
		
		SliderComponent roundingRadiusSlider = new SliderComponent();
		roundingRadiusSlider.setProperty(SliderComponent.HeaderProperty, GuiManager.roundingRadius.displayName);
		roundingRadiusSlider.setProperty(SliderComponent.MinimumProperty, GuiManager.roundingRadius.min_value);
		roundingRadiusSlider.setProperty(SliderComponent.MaximumProperty, GuiManager.roundingRadius.max_value);
		roundingRadiusSlider.bindProperty(SliderComponent.ValueProperty, GuiManager.roundingRadius, BindingMode.TwoWay);
		roundingRadiusSlider.setProperty(SliderComponent.StepProperty, GuiManager.roundingRadius.step);
		stackPanel.addChild(roundingRadiusSlider);
		
		SliderComponent lineThicknessSlider = new SliderComponent();
		lineThicknessSlider.setProperty(SliderComponent.HeaderProperty, GuiManager.lineThickness.displayName);
		lineThicknessSlider.setProperty(SliderComponent.MinimumProperty, GuiManager.lineThickness.min_value);
		lineThicknessSlider.setProperty(SliderComponent.MaximumProperty, GuiManager.lineThickness.max_value);
		lineThicknessSlider.bindProperty(SliderComponent.ValueProperty, GuiManager.lineThickness, BindingMode.TwoWay);
		lineThicknessSlider.setProperty(SliderComponent.StepProperty, GuiManager.lineThickness.step);
		stackPanel.addChild(lineThicknessSlider);
		
		// Responsiveness
		StringComponent responsivenessHeader = new StringComponent("Responsiveness");
		responsivenessHeader.setProperty(UIElement.FontWeightProperty, FontManager.WEIGHT_BOLD);
		responsivenessHeader.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundHeaderColor);
		stackPanel.addChild(responsivenessHeader);
		
		CheckboxComponent customTitleScreenCheckbox = new CheckboxComponent();
		customTitleScreenCheckbox.setProperty(CheckboxComponent.HeaderProperty, GuiManager.enableCustomTitle.displayName);
		customTitleScreenCheckbox.bindProperty(CheckboxComponent.IsCheckedProperty, GuiManager.enableCustomTitle, BindingMode.TwoWay);
		stackPanel.addChild(customTitleScreenCheckbox);
		
		CheckboxComponent tooltipCheckbox = new CheckboxComponent();
		tooltipCheckbox.setProperty(CheckboxComponent.HeaderProperty, GuiManager.enableTooltips.displayName);
		tooltipCheckbox.bindProperty(CheckboxComponent.IsCheckedProperty, GuiManager.enableTooltips, BindingMode.TwoWay);
		stackPanel.addChild(tooltipCheckbox);

		setContent(stackPanel);
	}
}
