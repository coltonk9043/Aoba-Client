/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import static net.aoba.utils.render.TextureBank.gear;

import org.joml.Quaternionf;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Size;
import net.aoba.gui.colors.Color;
import net.aoba.gui.navigation.CloseableWindow;
import net.aoba.module.Module;
import net.aoba.settings.Setting;
import net.aoba.settings.types.BlocksSetting;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;

public class ModuleComponent extends Component {
	private final Module module;

	private CloseableWindow lastSettingsTab = null;
	private boolean spinning = false;
	private float spinAngle = 0;

	public ModuleComponent(Module module) {

		header = module.getName();
		this.module = module;
		tooltip = module.getDescription();
		setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 30.0f);
	}

	@Override
	public void update() {
		super.update();
		if (spinning) {
			spinAngle = (spinAngle + 5) % 360;
		}
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		MatrixStack matrixStack = drawContext.getMatrices();

		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();

		if (header != null) {
			if (module.isDetectable(AOBA.moduleManager.antiCheat.getValue())) {
				Render2D.drawString(drawContext, header, actualX, actualY + 8, Colors.GRAY);
			} else {
				Render2D.drawString(drawContext, header, actualX, actualY + 8, module.state.getValue() ? 0x00FF00
						: hovered ? GuiManager.foregroundColor.getValue().getColorAsInt() : 0xFFFFFF);
			}
		}

		if (module.hasSettings()) {
			Color hudColor = GuiManager.foregroundColor.getValue();

			if (spinning) {
				matrixStack.push();
				matrixStack.translate((actualX + actualWidth - 8), (actualY + 14), 0);
				matrixStack.multiply(new Quaternionf().rotateZ((float) Math.toRadians(spinAngle)));
				matrixStack.translate(-(actualX + actualWidth - 8), -(actualY + 14), 0);
				Render2D.drawTexturedQuad(drawContext, gear, (actualX + actualWidth - 16), (actualY + 6), 16, 16,
						hudColor);
				matrixStack.pop();
			} else {
				Render2D.drawTexturedQuad(drawContext, gear, (actualX + actualWidth - 16), (actualY + 6), 16, 16,
						hudColor);
			}
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if ((event.button == MouseButton.LEFT && event.action == MouseAction.DOWN)
				|| (event.button == MouseButton.RIGHT && event.action == MouseAction.DOWN)) {
			if (hovered) {
				float mouseX = (float) event.mouseX;
				float actualX = actualSize.getX();
				float actualY = actualSize.getY();
				float actualWidth = actualSize.getWidth();

				boolean isOnOptionsButton = (mouseX >= (actualX + actualWidth - 34)
						&& mouseX <= (actualX + actualWidth));
				if (isOnOptionsButton || event.button == MouseButton.RIGHT) {
					spinning = true;
					if (lastSettingsTab == null) {
						lastSettingsTab = new CloseableWindow(module.getName(), actualX + actualWidth + 1, actualY);
						lastSettingsTab.setMinWidth(320.0f);
						StackPanelComponent stackPanel = new StackPanelComponent();

						StringComponent titleComponent = new StringComponent(module.getName() + " Settings");
						titleComponent.setIsHitTestVisible(false);
						stackPanel.addChild(titleComponent);

						stackPanel.addChild(new SeparatorComponent());

						KeybindComponent keybindComponent = new KeybindComponent(module.getBind());
						stackPanel.addChild(keybindComponent);

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
							} else {
								c = null;
							}

							if (c != null) {
								stackPanel.addChild(c);
							}
						}

						lastSettingsTab.addChild(stackPanel);

						lastSettingsTab.setOnClose(() -> {
							spinning = false;
						});

						lastSettingsTab.setMinWidth(250.0f);
						lastSettingsTab.setMaxWidth(600f);
						Aoba.getInstance().guiManager.addWindow(lastSettingsTab, "Modules");
						lastSettingsTab.initialize();
						spinning = true;
					} else {
						Aoba.getInstance().guiManager.removeWindow(lastSettingsTab, "Modules");
						spinning = false;
						lastSettingsTab = null;
					}
				} else {
					if (!module.isDetectable(AOBA.moduleManager.antiCheat.getValue()))
						module.toggle();
				}

				event.cancel();
			}
		}
	}
}
