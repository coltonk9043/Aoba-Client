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
    String pingText = null;
    
    public PingHud(int x, int y) {
        super("PingHud", x, y, 0, 32);
        resizeable = false;
        this.minHeight = 32f;
        this.maxHeight = 32f;
    }

    @Override
   	public void update() {
    	 ClientPlayNetworkHandler networkHandler = MC.getNetworkHandler();
         if (networkHandler != null && MC.player != null) {
             int ping = networkHandler.getPlayerListEntry(MC.player.getUuid()).getLatency();
             pingText = "Ping: " + ping + " ms";
              
             int textWidth = MC.textRenderer.getWidth(pingText);
             setWidth(textWidth * 2);
         }else
        	 pingText = null;
    }
    
    
    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);

        if (pingText != null && getVisible()) {
            Rectangle pos = position.getValue();
            if (pos.isDrawable()) {
            	Render2D.drawString(drawContext, pingText, pos.getX(), pos.getY(), GuiManager.foregroundColor.getValue().getColorAsInt());
            }
        }
    }
}
