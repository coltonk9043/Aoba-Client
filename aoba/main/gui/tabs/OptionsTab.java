package aoba.main.gui.tabs;

import aoba.main.gui.Tab;
import aoba.main.gui.elements.SliderComponent;
import aoba.main.gui.elements.StringComponent;
import net.minecraft.client.Minecraft;

public class OptionsTab extends Tab {

	private StringComponent uiSettingsSlider = new StringComponent(0, "UI Settings", this);
	private SliderComponent rSlider = new SliderComponent(1,"Red", this);
	private SliderComponent gSlider = new SliderComponent(2,"Green", this);
	private SliderComponent bSlider = new SliderComponent(3,"Blue", this);
	// 
	public OptionsTab(String title, int x, int y) {
		super(title, x, y);
		this.setWidth(70);
		this.addChild(uiSettingsSlider);
		this.addChild(rSlider);
		this.addChild(gSlider);
		this.addChild(bSlider);
	}
	
	@Override
	public void preupdate() {
		Minecraft mc = Minecraft.getInstance();
		rSlider.setText("Red: " + (int)(255 * rSlider.getSliderPosition()));
		gSlider.setText("Green: " + (int)(255 * gSlider.getSliderPosition()));
		bSlider.setText("Blue: " + (int)(255 * bSlider.getSliderPosition()));
		//(float)(255 * rSlider.getSliderPosition()) / 255
		rSlider.setColor(1f, 0f, 0f);
		gSlider.setColor(0f, (float)(255 * gSlider.getSliderPosition()) / 255, 0f);
		bSlider.setColor(0f, 0f, (float)(255 * bSlider.getSliderPosition()) / 255);
		
		Minecraft.getInstance().aoba.hm.setColor((int)(255 * rSlider.getSliderPosition()), (int)(255 * gSlider.getSliderPosition()), (int)(255 * bSlider.getSliderPosition()));
	}
}
