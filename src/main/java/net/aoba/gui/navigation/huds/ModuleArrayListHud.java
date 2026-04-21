/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.huds;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.ResizeMode;
import net.aoba.gui.types.TextAlign;
import net.aoba.module.Module;
import net.aoba.managers.SettingManager;
import net.aoba.settings.types.EnumSetting;
import net.aoba.rendering.Renderer2D;
import net.minecraft.client.gui.Font;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class ModuleArrayListHud extends HudWindow {
	private final EnumSetting<TextAlign> textAlign = EnumSetting.<TextAlign>builder().id("ModuleArrayListHudText_TextAlign")
			.displayName("Text Align").description("Text Alignment").defaultValue(TextAlign.Left).build();

	public ModuleArrayListHud(int x, int y) {
		super("ModuleArrayListHud", x, y);
		resizeMode = ResizeMode.None;

		SettingManager.registerSetting(textAlign);

		// Calculate max possible width.
		float newWidth = 0;
		for (Module mod : AOBA.moduleManager.modules) {
			float nameWidth = Renderer2D.getStringWidth(mod.getName(), GuiManager.fontSetting.getValue().getRenderer());
			if (nameWidth > newWidth)
				newWidth = nameWidth;
		}
		
		setProperty(UIElement.WidthProperty, newWidth);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (getProperty(UIElement.IsHoveredProperty) && event.button == MouseButton.RIGHT && event.action == MouseAction.DOWN) {
			TextAlign currentValue = textAlign.getValue();
			TextAlign[] enumConstants = currentValue.getDeclaringClass().getEnumConstants();
			int currentIndex = Arrays.asList(enumConstants).indexOf(currentValue);
			int enumCount = enumConstants.length;
			currentIndex = (currentIndex + 1) % enumCount;

			textAlign.setValue(enumConstants[currentIndex]);
		}
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		if (isVisible) {
			Rectangle pos = position.getValue();

			AtomicInteger iteration = new AtomicInteger(0);
				Font font = GuiManager.fontSetting.getValue().getRenderer();
				Stream<Module> moduleStream = AOBA.moduleManager.modules.stream().filter(s -> s.state.getValue())
						.sorted(Comparator.comparing((mod) -> mod.getName()));

				switch (textAlign.getValue()) {
				case Left:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.y() + 10 + (iteration.get() * 20);
						renderer.drawString(mod.getName(), pos.x(), yPosition,
								GuiManager.foregroundColor.getValue(), font);
						iteration.incrementAndGet();
					});
					break;
				case Center:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.y() + 10 + (iteration.get() * 20);
						float centerTextWidth = Renderer2D.getStringWidth(mod.getName(), font) / 2.0f;
						renderer.drawString(mod.getName(),
								pos.x() + (pos.width() / 2.0f) - centerTextWidth, yPosition,
								GuiManager.foregroundColor.getValue(), font);
						iteration.incrementAndGet();
					});
					break;
				case Right:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.y() + 10 + (iteration.get() * 20);
						float rightTextWidth = Renderer2D.getStringWidth(mod.getName(), font);
						renderer.drawString(mod.getName(), pos.x() + pos.width() - rightTextWidth,
								yPosition, GuiManager.foregroundColor.getValue(), font);
						iteration.incrementAndGet();
					});
					break;
				}
		}

		super.draw(renderer, partialTicks);
	}
}
