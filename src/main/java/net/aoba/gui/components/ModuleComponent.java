/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.aoba.settings.types.*;
import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.navigation.CloseableWindow;
import net.aoba.gui.types.BindingMode;
import net.aoba.gui.types.GridDefinition;
import net.aoba.gui.types.SizeToContent;
import net.aoba.gui.types.Thickness;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.gui.types.GridDefinition.RelativeUnit;
import net.aoba.module.AntiCheat;
import net.aoba.module.Module;
import net.aoba.rendering.shaders.Shader;
import net.aoba.rendering.utils.PolygonBank;
import net.aoba.settings.Setting;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class ModuleComponent extends Component {
	private static final Shader DEFAULT_SHADER = Shader.solid(Colors.Transparent);
	private static final Map<Module, CloseableWindow> openSettingsWindows = new HashMap<>();

	private final StringComponent nameComponent;
	private final RectangleComponent toggleRectangleComponent;
	private final PolygonComponent gearComponent;
	private final Consumer<Boolean> stateListener = this::moduleStateChanged;
	private final Consumer<AntiCheat> antiCheatListener = _ -> updateStyling();

	public static final UIProperty<Module> ModuleProperty = new UIProperty<>("Module", null, false, false, ModuleComponent::onModulePropertyChanged);
	
	private static void onModulePropertyChanged(UIElement sender, Module oldValue, Module newValue) {
		if(sender instanceof ModuleComponent moduleComponent) {
			moduleComponent.onModuleChanged(oldValue, newValue);
			if (newValue == null) {
				moduleComponent.setProperty(UIElement.ToolTipProperty, null);
				moduleComponent.nameComponent.setProperty(StringComponent.TextProperty, "");
				moduleComponent.gearComponent.setProperty(UIElement.IsVisibleProperty, false);
				moduleComponent.updateStyling();
				return;
			}
			moduleComponent.setProperty(UIElement.ToolTipProperty, newValue.getDescription());
			moduleComponent.nameComponent.setProperty(StringComponent.TextProperty, newValue.getName());
			moduleComponent.gearComponent.setProperty(UIElement.IsVisibleProperty, newValue.hasSettings());
			moduleComponent.updateStyling();
		}
	}
	
	private void onModuleChanged(Module oldValue, Module newValue) {
		if(oldValue != null)
			oldValue.state.removeOnUpdate(stateListener);

		if(newValue != null)
			newValue.state.addOnUpdate(stateListener);
	}
	
	private void moduleStateChanged(Boolean state) {
		updateStyling();
	}

	public ModuleComponent() {
		setProperty(UIElement.CursorProperty, CursorStyle.Click);
		setProperty(UIElement.BorderProperty, Shader.solid(Colors.Transparent));
		setProperty(UIElement.BorderThicknessProperty, 0f);
		
		toggleRectangleComponent = new RectangleComponent();
		toggleRectangleComponent.setProperty(UIElement.PaddingProperty, new Thickness(8f, 6f));
		toggleRectangleComponent.bindProperty(UIElement.CornerRadiusProperty, GuiManager.roundingRadius);
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));
		grid.setProperty(UIElement.MarginProperty, null);
		
		nameComponent = new StringComponent();
		nameComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		nameComponent.setProperty(UIElement.IsHitTestVisibleProperty, true);
		nameComponent.setOnClicked(this::onModuleTextClicked);

		grid.addChild(nameComponent);
		gearComponent = new PolygonComponent(PolygonBank.GEAR);
		gearComponent.setProperty(UIElement.WidthProperty, 16f);
		gearComponent.setProperty(UIElement.HeightProperty, 16f);
		gearComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		gearComponent.setOnClicked(this::onGearClicked);
		grid.addChild(gearComponent);

		toggleRectangleComponent.setContent(grid);
		setContent(toggleRectangleComponent);
		updateStyling();

		AOBA.moduleManager.antiCheat.addOnUpdate(antiCheatListener);
	}

	@Override
	public void dispose() {
		Module module = getProperty(ModuleProperty);
		if (module != null)
			module.state.removeOnUpdate(stateListener);
		AOBA.moduleManager.antiCheat.removeOnUpdate(antiCheatListener);
		super.dispose();
	}
	
	private void onModuleTextClicked(MouseClickEvent e) {
		if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
			Module module = getProperty(ModuleComponent.ModuleProperty);
			
			if(module == null)
				return;
			
			if (!module.isDetectable(AOBA.moduleManager.antiCheat.getValue())) {
				module.toggle();
			}
			e.cancel();
		}
	}

	private void onGearClicked(MouseClickEvent e) {
		if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
			Module module = getProperty(ModuleComponent.ModuleProperty);
			if (module == null)
				return;

			CloseableWindow existing = openSettingsWindows.get(module);
			if (existing != null) {
				Aoba.getInstance().guiManager.removeWindow(existing, "Modules");
				existing.dispose();
				openSettingsWindows.remove(module);
				e.cancel();
				return;
			}

			float actualX = actualSize.x();
			float actualY = actualSize.y();
			float actualWidth = actualSize.width();

			CloseableWindow settingsTab = new CloseableWindow(module.getName(), actualX + actualWidth + 1, actualY);
			settingsTab.setProperty(UIElement.MinWidthProperty, 320.0f);
			StackPanelComponent stackPanel = new StackPanelComponent();
			stackPanel.setProperty(UIElement.MarginProperty, new Thickness(4f));
			stackPanel.setSpacing(8f);
			StringComponent titleComponent = new StringComponent(module.getName() + " Settings");
			titleComponent.setProperty(UIElement.IsHitTestVisibleProperty, false);
			stackPanel.addChild(titleComponent);

			stackPanel.addChild(new SeparatorComponent());

			for (Setting<?> setting : module.getSettings()) {
				if (setting == module.state)
					continue;

				UIElement c;
				if (setting instanceof FloatSetting floatSetting) {
					SliderComponent slider = new SliderComponent();
					slider.setProperty(SliderComponent.MinimumProperty, floatSetting.min_value);
					slider.setProperty(SliderComponent.MaximumProperty, floatSetting.max_value);
					slider.setProperty(SliderComponent.StepProperty, floatSetting.step);
					slider.bindProperty(SliderComponent.ValueProperty, setting, BindingMode.TwoWay);
					slider.setProperty(SliderComponent.HeaderProperty, setting.displayName);
					c = slider;
				} else if (setting instanceof BooleanSetting) {
					CheckboxComponent boolCheckbox = new CheckboxComponent();
					boolCheckbox.setProperty(CheckboxComponent.HeaderProperty, setting.displayName);
					boolCheckbox.bindProperty(CheckboxComponent.IsCheckedProperty, setting, BindingMode.TwoWay);
					c = boolCheckbox;
				} else if (setting instanceof ShaderSetting) {
					ExpanderComponent shaderExpander = new ExpanderComponent(setting.displayName);
					ShaderComponent shaderControl = new ShaderComponent();
					shaderControl.bindProperty(ShaderComponent.ShaderProperty, setting, BindingMode.TwoWay);
					shaderExpander.setContent(shaderControl);
					c = shaderExpander;
				} else if (setting instanceof ColorSetting) {
					ColorPickerComponent colorPicker = new ColorPickerComponent();
					colorPicker.bindProperty(ColorPickerComponent.ColorProperty, setting, BindingMode.TwoWay);
					c = colorPicker;
				} else if (setting instanceof BlocksSetting) {
					ExpanderComponent blocksExpander = new ExpanderComponent(setting.displayName);
					blocksExpander.setContent(new BlocksComponent((BlocksSetting) setting));
					c = blocksExpander;
				} else if (setting instanceof EnumSetting) {
					StackPanelComponent comboStack = new StackPanelComponent();

					StringComponent enumHeader = new StringComponent();
					enumHeader.setProperty(StringComponent.TextProperty, setting.displayName);
					comboStack.addChild(enumHeader);
					ComboBoxComponent comboBox = new ComboBoxComponent();
					comboBox.setProperty(ComboBoxComponent.ItemsSourceProperty, Arrays.asList(setting.getValue().getClass().getEnumConstants()));
					comboBox.bindProperty(ComboBoxComponent.SelectedItemProperty, setting, BindingMode.TwoWay);
					comboStack.addChild(comboBox);
					c = comboStack;
				} else if (setting instanceof HotbarSetting) {
					c = new HotbarComponent((HotbarSetting) setting);
				} else if(setting instanceof KeybindSetting) {
					KeybindComponent keybind = new KeybindComponent();
					keybind.setProperty(KeybindComponent.HeaderProperty, setting.displayName);
					keybind.bindProperty(KeybindComponent.SelectedKeyProperty, setting, BindingMode.TwoWay);
					c = keybind;
				} else {
					c = null;
				}

				if (c != null) {
					stackPanel.addChild(c);
				}
			}

			settingsTab.setContent(stackPanel);

			settingsTab.setProperty(UIElement.MinWidthProperty, 300.0f);
			settingsTab.setProperty(UIElement.MaxWidthProperty, 600f);
			settingsTab.setSizeToContent(SizeToContent.Height);
			settingsTab.setOnClose(() -> openSettingsWindows.remove(module));
			openSettingsWindows.put(module, settingsTab);
			Aoba.getInstance().guiManager.addWindow(settingsTab, "Modules");
			settingsTab.initialize();
			e.cancel();
		}
	}
	
	private void updateStyling() {
		Module module = getProperty(ModuleComponent.ModuleProperty);

		if(module == null) {
			nameComponent.setProperty(ForegroundProperty, Shader.solid(Colors.White));
			toggleRectangleComponent.unbindProperty(UIElement.BackgroundProperty);
			toggleRectangleComponent.setProperty(UIElement.BackgroundProperty, DEFAULT_SHADER);
			return;
		}

		if (module.isDetectable(AOBA.moduleManager.antiCheat.getValue())) {
			nameComponent.setProperty(ForegroundProperty, Shader.solid(Colors.Gray));
		} else {
			nameComponent.setProperty(ForegroundProperty, Shader.solid(Colors.White));
		}

		// Set state rectangle color based on module state.
		if(module.state.getValue()) {
			toggleRectangleComponent.unbindProperty(UIElement.BackgroundProperty);
			toggleRectangleComponent.bindProperty(UIElement.BackgroundProperty, GuiManager.buttonBackgroundColor);
		}else {
			toggleRectangleComponent.unbindProperty(UIElement.BackgroundProperty);
			toggleRectangleComponent.setProperty(UIElement.BackgroundProperty, DEFAULT_SHADER);
		}
	}
}
