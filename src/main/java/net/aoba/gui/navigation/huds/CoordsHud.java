package net.aoba.gui.navigation.huds;

import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CoordsHud extends HudWindow {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public CoordsHud(int x, int y) {
        super("CoordsHud", x, y, 0, 0);
        resizeable = false;
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);

        if (this.visible) {
            Rectangle pos = position.getValue();
            if (pos.isDrawable()) {
                String coordsText = String.format("X: %.1f, Y: %.1f, Z: %.1f",
                        MC.player.getX(), MC.player.getY(), MC.player.getZ());

                int textWidth = MC.textRenderer.getWidth(coordsText);
                int textHeight = MC.textRenderer.fontHeight;

                setWidth(textWidth * 2);
                setHeight(textHeight * 2);

                Render2D.drawString(drawContext, coordsText, pos.getX(), pos.getY(), GuiManager.foregroundColor.getValue().getColorAsInt());
            }
        }
    }
}
