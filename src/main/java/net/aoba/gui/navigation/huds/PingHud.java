package net.aoba.gui.navigation.huds;

import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class PingHud extends HudWindow {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public PingHud(int x, int y) {
        super("PingHud", x, y, 0, 0);
        resizeable = false;
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);

        if (getVisible()) {
            Rectangle pos = position.getValue();
            if (pos.isDrawable()) {
                ClientPlayNetworkHandler networkHandler = MC.getNetworkHandler();
                if (networkHandler != null && MC.player != null) {
                    int ping = networkHandler.getPlayerListEntry(MC.player.getUuid()).getLatency();
                    String pingText = "Ping: " + ping + " ms";

                    int textWidth = MC.textRenderer.getWidth(pingText);
                    int textHeight = MC.textRenderer.fontHeight;

                    setWidth(textWidth * 2);
                    setHeight(textHeight * 2);

                    Render2D.drawString(drawContext, pingText, pos.getX(), pos.getY(), GuiManager.foregroundColor.getValue().getColorAsInt());
                }
            }
        }
    }
}
