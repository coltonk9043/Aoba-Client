package net.aoba.gui.tabs;

import net.aoba.gui.ClickGuiTab;
import net.aoba.gui.elements.StringComponent;
import net.minecraft.client.MinecraftClient;

public class InfoTab extends ClickGuiTab {

	private StringComponent positionComponent = new StringComponent("X: Y: Z:", this);
	private StringComponent timeComponent = new StringComponent("Time:", this);
	private StringComponent fpsDayComponent = new StringComponent("FPS: Day:", this);

	// 
	public InfoTab(String title, int x, int y) {
		super(title, x, y);
		this.setWidth(190);
		this.addChild(positionComponent);
		this.addChild(timeComponent);
		this.addChild(fpsDayComponent);
	}
	
	@Override
	public void preupdate() {
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
		positionComponent.setText("XYZ: " + (int)mc.player.getBlockX() + ", " + (int)mc.player.getBlockY() + ", " + (int)mc.player.getBlockZ());
		timeComponent.setText("Time: " + timeString);
		fpsDayComponent.setText("FPS: " + mc.fpsDebugString.split(" ", 2)[0] + " Day: " + (int) (mc.world.getTime() / 24000));
		
		int newWidth = (int)(mc.textRenderer.getWidth(positionComponent.getText()) * 2) + 20;
		if(this.getWidth()!= newWidth) {
			if(newWidth >= 190) {
				this.setWidth(newWidth);
			}else {
				this.setWidth(190);
			}
		}
	}
}
