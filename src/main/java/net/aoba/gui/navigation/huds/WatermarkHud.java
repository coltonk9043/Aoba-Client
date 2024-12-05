package net.aoba.gui.navigation.huds;

import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.ResizeMode;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;

public class WatermarkHud extends HudWindow {

	public WatermarkHud(int x, int y) {
		super("WatermarkHud", x, y, 50, 24);
		this.minWidth = 50f;
		this.minHeight = 20f;
		this.maxHeight = 20f;
		resizeMode = ResizeMode.None;
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (isVisible()) {
			Rectangle pos = position.getValue();
			if (pos.isDrawable()) {
				String watermarkText = "Aoba Client";
				Render2D.drawString(drawContext, watermarkText, pos.getX(), pos.getY(),
						GuiManager.foregroundColor.getValue().getColorAsInt());
			}
		}

		super.draw(drawContext, partialTicks);
	}

}
