package net.aoba.gui.navigation.huds;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.module.Module;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.EnumSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import net.aoba.gui.TextAlign;

public class ModuleArrayListHud extends HudWindow {
	private EnumSetting<TextAlign> textAlign = new EnumSetting<TextAlign>("ModuleArrayListHudText_TextAlign", "Text Align", "Text Alignment", TextAlign.Left);
	
	public ModuleArrayListHud(int x, int y) {
		super("ModuleArrayListHud", x, y, 0, 0);
		resizeable = false;
		
		SettingManager.registerSetting(textAlign, Aoba.getInstance().settingManager.configContainer);
		
		// Calculate max possible width.
		float newWidth = 0;
		for(Module mod : AOBA.moduleManager.modules) {
			float nameWidth = Render2D.getStringWidth(mod.getName());
			if(nameWidth > newWidth)
				newWidth = nameWidth;	
		}
		
		this.setWidth(newWidth);
	}
	
	@Override
	public void OnMouseClick(MouseClickEvent event) {
		super.OnMouseClick(event);
		
		if(this.isMouseOver && event.button == MouseButton.RIGHT && event.action == MouseAction.DOWN) {
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
			if (mod.getState()) {
				totalHeight += 20;
			}
		}
		this.setHeight(totalHeight + 10);
	}
	
	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (getVisible()) {
			Rectangle pos = position.getValue();

			if (pos.isDrawable()) {
				AtomicInteger iteration = new AtomicInteger(0);
				Stream<Module> moduleStream = AOBA.moduleManager.modules.stream().filter(Module::getState)
						.sorted(Comparator.comparing((mod) -> ((Module)mod).getName()));
				
				switch(textAlign.getValue()) {
				case TextAlign.Left:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.getY().floatValue() + 10 + (iteration.get() * 20);
						Render2D.drawString(drawContext, mod.getName(), pos.getX(), yPosition,
								GuiManager.foregroundColor.getValue().getColorAsInt());
						iteration.incrementAndGet();
					});
					break;
				case TextAlign.Center:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.getY().floatValue() + 10 + (iteration.get() * 20);
						float centerTextWidth = Render2D.getStringWidth(mod.getName());
						Render2D.drawString(drawContext, mod.getName(), pos.getX() + (pos.getWidth() / 2.0f) - centerTextWidth, yPosition,
								GuiManager.foregroundColor.getValue().getColorAsInt());
						iteration.incrementAndGet();
					});
					break;
				case TextAlign.Right:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.getY().floatValue() + 10 + (iteration.get() * 20);
						float rightTextWidth = Render2D.getStringWidth(mod.getName()) * 2;
						Render2D.drawString(drawContext, mod.getName(), pos.getX() + pos.getWidth() - rightTextWidth, yPosition,
								GuiManager.foregroundColor.getValue().getColorAsInt());
						iteration.incrementAndGet();
					});
					break;
				}
				
			}
		}

		super.draw(drawContext, partialTicks);
	}
}
