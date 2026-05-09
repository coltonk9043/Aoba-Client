package net.aoba.gui.navigation.windows;

import java.util.LinkedHashMap;
import java.util.Map;

import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.components.ModuleComponent;
import net.aoba.gui.components.ScrollComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.TextBoxComponent;
import net.aoba.gui.navigation.Popup;
import net.aoba.gui.navigation.Window;
import net.aoba.gui.types.HorizontalAlignment;
import net.aoba.gui.types.ResizeMode;
import net.aoba.gui.types.SizeToContent;
import net.aoba.gui.types.Thickness;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.rendering.shaders.Shader;
import net.aoba.module.Module;

public class SearchWindow extends Window {

	private TextBoxComponent searchTextBox;
	private ScrollComponent dropdownScroll;
	private Map<Module, ModuleComponent> moduleRows;

	public SearchWindow() {
		super("Search", MC.screen.width / 2 - 250, 990);

		this.resizeMode = ResizeMode.None;
		this.sizeToContent = SizeToContent.Height;
		unbindProperty(UIElement.BackgroundProperty);
		unbindProperty(UIElement.BorderProperty);
		unbindProperty(UIElement.PaddingProperty);
		setProperty(UIElement.BackgroundProperty, Shader.solid(Colors.Transparent));
		setProperty(UIElement.BorderProperty, Shader.solid(Colors.Transparent));
		setProperty(UIElement.PaddingProperty, new Thickness(2f)); // Small padding for moving.
		setProperty(UIElement.BorderThicknessProperty, 0f);
		setProperty(UIElement.WidthProperty, 350f);

		searchTextBox = new TextBoxComponent();
		searchTextBox.setOnFocus(() -> {
			String text = searchTextBox.getProperty(TextBoxComponent.TextProperty);
			onSearchTextChanged(text);
		});
		searchTextBox.bindProperty(UIElement.BackgroundProperty, GuiManager.windowBackgroundColor);
		searchTextBox.bindProperty(UIElement.BorderProperty, GuiManager.windowBorderColor);
		searchTextBox.setProperty(UIElement.CornerRadiusProperty, 30f);
		searchTextBox.setProperty(TextBoxComponent.FontSizeProperty, 16f);
		searchTextBox.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Stretch);
		searchTextBox.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Stretch);
		searchTextBox.setProperty(TextBoxComponent.PlaceholderText, "Search...");
		searchTextBox.setOnTextChanged(s -> onSearchTextChanged(s));

		this.setContent(searchTextBox);
	}

	private void onSearchTextChanged(String text) {
		// Create popup if it exists.
		Popup currentPopup = Aoba.getInstance().guiManager.getPopup();
		if (currentPopup == null || currentPopup.getContent() != dropdownScroll) {
			moduleRows = new LinkedHashMap<>();
			StackPanelComponent itemList = new StackPanelComponent();
			itemList.setSpacing(4f);

			for (Module module : AOBA.moduleManager.modules) {
				ModuleComponent row = new ModuleComponent();
				row.setProperty(ModuleComponent.ModuleProperty, module);
				moduleRows.put(module, row);
				itemList.addChild(row);
			}

			dropdownScroll = new ScrollComponent();
			dropdownScroll.setProperty(UIElement.MaxHeightProperty, 500f);
			dropdownScroll.setContent(itemList);
			Aoba.getInstance().guiManager.openPopup(this, dropdownScroll, Popup.PlacementMode.Bottom);
		}

		// Show/Hide rows depending on predicate.
		String filter = text == null ? "" : text.toLowerCase();
		for (Map.Entry<Module, ModuleComponent> entry : moduleRows.entrySet()) {
			boolean matches = entry.getKey().getName().toLowerCase().contains(filter);
			entry.getValue().setProperty(UIElement.IsVisibleProperty, matches);
		}
	}

}