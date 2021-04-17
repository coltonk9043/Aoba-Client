package aoba.main.gui.tabs;

import aoba.main.gui.ClickGuiTab;
import aoba.main.gui.elements.CheckboxComponent;
import aoba.main.gui.elements.SliderComponent;
import aoba.main.gui.elements.StringComponent;
import aoba.main.settings.BooleanSetting;
import aoba.main.settings.SliderSetting;

public class OptionsTab extends ClickGuiTab {

	private StringComponent uiSettingsString = new StringComponent("UI Settings", this, true);
	private SliderComponent hueSlider;
	private CheckboxComponent rainbowBox;
	private SliderComponent effectSpeed;
	private CheckboxComponent armorHud;
	
	public OptionsTab(String title, int x, int y, SliderSetting hue, BooleanSetting rainbow, BooleanSetting ah, SliderSetting es) {
		super(title, x, y);
		this.setWidth(90);
		this.addChild(uiSettingsString);
		this.hueSlider = new SliderComponent( this, hue);
		this.addChild(hueSlider);
		this.rainbowBox = new CheckboxComponent(this, rainbow);
		this.addChild(rainbowBox);
		this.effectSpeed = new SliderComponent(this, es);
		this.addChild(effectSpeed);
		this.armorHud = new CheckboxComponent(this, ah);
		this.addChild(armorHud);
	}

	@Override
	public void preupdate() {
	}
	
	public void setHueSliderPosition(float position) {
		this.hueSlider.setSliderPosition(position);
	}
}
