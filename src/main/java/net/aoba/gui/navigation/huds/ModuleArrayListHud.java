/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.huds;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.ResizeMode;
import net.aoba.gui.TextAlign;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.module.Module;
import net.aoba.managers.SettingManager;
import net.aoba.settings.types.EnumSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

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
			float nameWidth = Render2D.getStringWidth(mod.getName());
			if (nameWidth > newWidth)
				newWidth = nameWidth;
		}

		setWidth(newWidth);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (hovered && event.button == MouseButton.RIGHT && event.action == MouseAction.DOWN) {
			TextAlign currentValue = textAlign.getValue();
			TextAlign[] enumConstants = currentValue.getDeclaringClass().getEnumConstants();
			int currentIndex = java.util.Arrays.asList(enumConstants).indexOf(currentValue);
			int enumCount = enumConstants.length;
			currentIndex = (currentIndex + 1) % enumCount;

			textAlign.setValue(enumConstants[currentIndex]);
		}
	}

	@Override
	public void update() {
		super.update();
		int totalHeight = 0;
		for (Module mod : AOBA.moduleManager.modules) {
			if (mod.state.getValue()) {
				totalHeight += 20;
			}
		}
		// this.setHeight(totalHeight + 10);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (isVisible()) {
			Rectangle pos = position.getValue();

			if (pos.isDrawable()) {
				AtomicInteger iteration = new AtomicInteger(0);
				Stream<Module> moduleStream = AOBA.moduleManager.modules.stream().filter(s -> s.state.getValue())
						.sorted(Comparator.comparing((mod) -> mod.getName()));

				switch (textAlign.getValue()) {
				case Left:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.getY().floatValue() + 10 + (iteration.get() * 20);
						Render2D.drawString(drawContext, mod.getName(), pos.getX(), yPosition,
								GuiManager.foregroundColor.getValue().getColorAsInt());
						iteration.incrementAndGet();
					});
					break;
				case Center:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.getY().floatValue() + 10 + (iteration.get() * 20);
						float centerTextWidth = Render2D.getStringWidth(mod.getName()) / 2.0f;
						Render2D.drawString(drawContext, mod.getName(),
								pos.getX() + (pos.getWidth() / 2.0f) - centerTextWidth, yPosition,
								GuiManager.foregroundColor.getValue().getColorAsInt());
						iteration.incrementAndGet();
					});
					break;
				case Right:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.getY().floatValue() + 10 + (iteration.get() * 20);
						float rightTextWidth = Render2D.getStringWidth(mod.getName());
						Render2D.drawString(drawContext, mod.getName(), pos.getX() + pos.getWidth() - rightTextWidth,
								yPosition, GuiManager.foregroundColor.getValue().getColorAsInt());
						iteration.incrementAndGet();
					});
					break;
				}

			}
		}

		super.draw(drawContext, partialTicks);
	}
}
