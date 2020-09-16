package aoba.main.gui.tabs;

import aoba.main.gui.Tab;
import aoba.main.gui.elements.SliderComponent;
import aoba.main.gui.elements.StringComponent;
import net.minecraft.client.Minecraft;

public class InfoTab extends Tab {

	private StringComponent positionComponent = new StringComponent(0, "X: Y: Z:", this);
	private StringComponent timeComponent = new StringComponent(1, "Time:", this);
	private StringComponent fpsDayComponent = new StringComponent(2, "FPS: Day:", this);

	// 
	public InfoTab(String title, int x, int y) {
		super(title, x, y);
		this.setWidth(95);
		this.addChild(positionComponent);
		this.addChild(timeComponent);
		this.addChild(fpsDayComponent);
	}
	
	@Override
	public void preupdate() {
		Minecraft mc = Minecraft.getInstance();

		int time = ((int)mc.world.getDayTime() + 6000)% 24000;
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
		positionComponent.setText("XYZ: " + (int)mc.player.getPosX() + ", " + (int)mc.player.getPosY() + ", " + (int)mc.player.getPosZ());
		timeComponent.setText("Time: " + timeString);
		fpsDayComponent.setText("FPS: " + mc.debug.split(" ", 2)[0] + " Day: " + (int) (mc.world.getGameTime() / 24000));
		
		int newWidth = (int)mc.fontRenderer.getStringWidth(positionComponent.getText()) + 10;
		if(this.getWidth()!= newWidth) {
			if(newWidth >= 95) {
				this.setWidth(newWidth);
			}else {
				this.setWidth(95);
			}
		}
	}
}
