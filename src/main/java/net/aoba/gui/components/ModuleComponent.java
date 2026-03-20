/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import static net.aoba.utils.render.TextureBank.gear;

import net.aoba.settings.types.*;
import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GridDefinition;
import net.aoba.gui.GridDefinition.RelativeUnit;
import net.aoba.gui.GuiManager;
import net.aoba.gui.VerticalAlignment;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.navigation.CloseableWindow;
import net.aoba.module.Module;
import net.aoba.settings.Setting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class ModuleComponent extends Component {
	private final Module module;
	private final StringComponent nameComponent;

	private CloseableWindow lastSettingsTab = null;

	public ModuleComponent(Module module) {
		this.module = module;
		tooltip = module.getDescription();
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));

		nameComponent = new StringComponent(module.getName());
		nameComponent.setVerticalAlignment(VerticalAlignment.Center);
		nameComponent.setIsHitTestVisible(true);
		nameComponent.setOnClicked(this::onModuleTextClicked);
		grid.addChild(nameComponent);

		ImageComponent gearComponent = new ImageComponent(gear);
		gearComponent.setWidth(16f);
		gearComponent.setHeight(16f);
		gearComponent.setVerticalAlignment(VerticalAlignment.Center);
		gearComponent.setVisible(module.hasSettings());
		gearComponent.setOnClicked(this::onGearClicked);
		grid.addChild(gearComponent);

		addChild(grid);
	}

	@Override
	public void update() {
		super.update();

		if (module.isDetectable(AOBA.moduleManager.antiCheat.getValue())) {
			nameComponent.setColor(Colors.Gray);
		} else if (module.state.getValue()) {
			nameComponent.setColor(Colors.Green);
		} else if (hovered) {
			nameComponent.setColor(GuiManager.foregroundColor.getValue());
		} else {
			nameComponent.setColor(Colors.White);
		}
	}

	private void onModuleTextClicked(MouseClickEvent e) {
		if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
			if (!module.isDetectable(AOBA.moduleManager.antiCheat.getValue()))
				module.toggle();
			e.cancel();
		}
	}

	private void onGearClicked(MouseClickEvent e) {
		if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
			if (lastSettingsTab == null) {
				float actualX = actualSize.getX();
				float actualY = actualSize.getY();
				float actualWidth = actualSize.getWidth();

				lastSettingsTab = new CloseableWindow(module.getName(), actualX + actualWidth + 1, actualY);
				lastSettingsTab.setMinWidth(320.0f);
				StackPanelComponent stackPanel = new StackPanelComponent();
				stackPanel.setSpacing(4f);
				StringComponent titleComponent = new StringComponent(module.getName() + " Settings");
				titleComponent.setIsHitTestVisible(false);
				stackPanel.addChild(titleComponent);

				stackPanel.addChild(new SeparatorComponent());

				for (Setting<?> setting : module.getSettings()) {
					if (setting == module.state)
						continue;

					Component c;
					if (setting instanceof FloatSetting) {
						c = new SliderComponent((FloatSetting) setting);
					} else if (setting instanceof BooleanSetting) {
						c = new CheckboxComponent((BooleanSetting) setting);
					} else if (setting instanceof ColorSetting) {
						c = new ColorPickerComponent((ColorSetting) setting);
					} else if (setting instanceof BlocksSetting) {
						c = new BlocksComponent((BlocksSetting) setting);
					} else if (setting instanceof EnumSetting) {
						c = new EnumComponent<>((EnumSetting) setting);
					} else if (setting instanceof HotbarSetting) {
						c = new HotbarComponent((HotbarSetting) setting);
					} else if(setting instanceof KeybindSetting) {
						c = new KeybindComponent((KeybindSetting) setting);
					} else {
						c = null;
					}

					if (c != null) {
						stackPanel.addChild(c);
					}
				}

				lastSettingsTab.addChild(stackPanel);

				lastSettingsTab.setMinWidth(300.0f);
				lastSettingsTab.setMaxWidth(600f);
				Aoba.getInstance().guiManager.addWindow(lastSettingsTab, "Modules");
				lastSettingsTab.initialize();
			} else {
				Aoba.getInstance().guiManager.removeWindow(lastSettingsTab, "Modules");
				lastSettingsTab = null;
			}
			e.cancel();
		}
	}
}
