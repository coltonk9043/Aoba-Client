package net.aoba.gui.navigation.huds;

import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.misc.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class FPSHud extends HudWindow {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public FPSHud(int x, int y) {
        super("FPSHud", x, y, 0, 0);
        resizeable = false;
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);

        if (this.visible) {
            Rectangle pos = position.getValue();
            if (pos.isDrawable()) {
                int fps = MC.getCurrentFps();
                String fpsText = "FPS: " + fps;

                int textWidth = MC.textRenderer.getWidth(fpsText);
                int textHeight = MC.textRenderer.fontHeight;

                setWidth(textWidth * 2);
                setHeight(textHeight * 2);

                Render2D.drawString(drawContext, fpsText, pos.getX(), pos.getY(), GuiManager.foregroundColor.getValue().getColorAsInt());
            }
        }
    }
}