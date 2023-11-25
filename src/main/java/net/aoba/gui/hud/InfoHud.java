package net.aoba.gui.hud;

import net.aoba.Aoba;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.utils.types.Vector2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class InfoHud extends AbstractHud {

	String positionText = "";
	String timeText = "";
	String fpsText = "";
	
	// 
	public InfoHud(int x, int y) {
		super("InfoHud", x, y, 190, 60);
		this.setVisible(false);
	}
	
	@Override
	public void update() {
		MinecraftClient mc = MinecraftClient.getInstance();

		int time = ((int)mc.world.getTime() + 6000)% 24000;
		String suffix = time >= 12000 ? "PM" : "AM";
		String timeString = (time / 10) % 1200 + "";
		for (int n = timeString.length(); n < 4; ++n) {
			timeString = "0" + timeString;
        }
		final String[] strsplit = timeString.split("");
		String hours = strsplit[0] + strsplit[1];
		if(hours.equalsIgnoreCase("00")) {
			hours = "12";
		}
		final int minutes = (int)Math.floor(Double.parseDouble(strsplit[2] + strsplit[3]) / 100.0 * 60.0);
		String sm = minutes + "";
        if (minutes < 10) {
            sm = "0" + minutes;
        }
		timeString = hours + ":" + sm.charAt(0) + sm.charAt(1) + suffix;
		positionText = "XYZ: " + (int)mc.player.getBlockX() + ", " + (int)mc.player.getBlockY() + ", " + (int)mc.player.getBlockZ();
		timeText = "Time: " + timeString;
		fpsText = "FPS: " + mc.fpsDebugString.split(" ", 2)[0] + " Day: " + (int) (mc.world.getTime() / 24000);
		
		int newWidth = (int)(mc.textRenderer.getWidth(positionText) * 2) + 20;
		if(this.getWidth()!= newWidth) {
			if(newWidth >= 190) {
				this.setWidth(newWidth);
			}else {
				this.setWidth(190);
			}
		}
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		if(this.visible) {
			MatrixStack matrixStack = drawContext.getMatrices();
			// Draws background depending on components width and height
			
			Vector2 pos = position.getValue();
			
			HudManager hudManager = Aoba.getInstance().hudManager;
			renderUtils.drawRoundedBox(matrixStack, pos.x, pos.y, width, height, 6, hudManager.backgroundColor.getValue());
			renderUtils.drawRoundedOutline(matrixStack, pos.x, pos.y, width, height, 6, hudManager.borderColor.getValue());
			
			renderUtils.drawString(drawContext, positionText, pos.x + 5, pos.y + 4, color);
			renderUtils.drawString(drawContext, timeText, pos.x + 5, pos.y + 24, color);
			renderUtils.drawString(drawContext, fpsText, pos.x + 5, pos.y + 44, color);
		}
	}
}
