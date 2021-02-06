package aoba.main.gui.tabs;

import aoba.main.gui.Tab;
import aoba.main.gui.elements.PopoutComponent;
import aoba.main.gui.elements.SliderComponent;
import aoba.main.gui.elements.StringComponent;
import aoba.main.misc.Utils;
import net.minecraft.client.Minecraft;

public class OptionsTab extends Tab {

	private StringComponent uiSettingsSlider = new StringComponent(0, "UI Settings", this);
	private PopoutComponent rgbPopout = new PopoutComponent(1, "RGB Settings", this);

	private SliderComponent rSlider = new SliderComponent(0, "Red", rgbPopout.getTabOpened());
	private SliderComponent gSlider = new SliderComponent(1, "Green", rgbPopout.getTabOpened());
	private SliderComponent bSlider = new SliderComponent(2, "Blue", rgbPopout.getTabOpened());

	//
	public OptionsTab(String title, int x, int y) {
		super(title, x, y);
		this.setWidth(90);
		this.addChild(uiSettingsSlider);
		rgbPopout.addComponent(rSlider);
		rgbPopout.addComponent(gSlider);
		rgbPopout.addComponent(bSlider);
		this.addChild(rgbPopout);
	}

	@Override
	public void preupdate() {
		Minecraft mc = Minecraft.getInstance();
		if(this.rgbPopout.isPopped()) {
			rSlider.setText("Red: " + (int)(255 * rSlider.getSliderPosition()));
			gSlider.setText("Green: " + (int)(255 * gSlider.getSliderPosition()));
			bSlider.setText("Blue: " + (int)(255 * bSlider.getSliderPosition()));
			rSlider.setColor((float)(255 * rSlider.getSliderPosition()) / 255, 0f, 0f);
			gSlider.setColor(0f, (float)(255 * gSlider.getSliderPosition()) / 255, 0f);
			bSlider.setColor(0f, 0f, (float)(255 * bSlider.getSliderPosition()) / 255);
			Minecraft.getInstance().aoba.hm.setColor((int)(255 * rSlider.getSliderPosition()), (int)(255 * gSlider.getSliderPosition()), (int)(255 * bSlider.getSliderPosition()));
		}
	}

	public void setRedSliderPosition(float position) {
		this.rSlider.setSliderPosition(position);
	}

	public void setGreenSliderPosition(float position) {
		this.gSlider.setSliderPosition(position);
	}

	public void setBlueSliderPosition(float position) {
		this.bSlider.setSliderPosition(position);
	}
}
