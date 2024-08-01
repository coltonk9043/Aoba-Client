package net.aoba.gui.navigation.huds;

import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class SpeedHud extends HudWindow {

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final double BLOCKS_TO_KM = 0.001;

    public SpeedHud(int x, int y) {
        super("SpeedHud", x, y, 0, 0);
        resizeable = false;
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);

        if (getVisible()) {
            Rectangle pos = position.getValue();
            if (pos.isDrawable()) {
                PlayerEntity player = MC.player;
                if (player != null) {
                    double dx = player.getX() - player.prevX;
                    double dz = player.getZ() - player.prevZ;
                    double dy = player.getY() - player.prevY;

                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                    double speed = distance * 20 * 3.6;

                    String speedText = String.format("Speed: %.2f km/h", speed);

                    int textWidth = MC.textRenderer.getWidth(speedText);
                    int textHeight = MC.textRenderer.fontHeight;

                    setWidth(textWidth * 2);
                    setHeight(textHeight * 2);

                    Render2D.drawString(drawContext, speedText, pos.getX(), pos.getY(), GuiManager.foregroundColor.getValue().getColorAsInt());
                }
            }
        }
    }
}
