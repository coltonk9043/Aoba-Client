package net.aoba.gui.navigation.huds;

import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;import net.minecraft.client.gui.DrawContext;

public class WatermarkHud extends HudWindow {

    public WatermarkHud(int x, int y) {
        super("WatermarkHud", x, y, 0, 20);
        
        resizeable = false;
        inheritHeightFromChildren = false;
        
        this.minHeight = 20f;
        this.maxHeight = 20f;
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        if (getVisible()) {
            Rectangle pos = position.getValue();
            if (pos.isDrawable()) {
                String watermarkText = "Aoba Client";

                int textWidth = MC.textRenderer.getWidth(watermarkText);
                int textHeight = MC.textRenderer.fontHeight;

                setWidth(textWidth * 2);
                setHeight(textHeight * 2);

                Render2D.drawString(drawContext, watermarkText, pos.getX(), pos.getY(), GuiManager.foregroundColor.getValue().getColorAsInt());
            }
        }
        
        super.draw(drawContext, partialTicks);
    }

}
