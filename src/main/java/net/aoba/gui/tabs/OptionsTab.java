package net.aoba.gui.tabs;

import net.aoba.gui.ClickGuiTab;
import net.aoba.gui.elements.CheckboxComponent;
import net.aoba.gui.elements.SliderComponent;
import net.aoba.gui.elements.StringComponent;
import net.aoba.settings.BooleanSetting;
import net.aoba.settings.SliderSetting;

public class OptionsTab extends ClickGuiTab {

	private StringComponent uiSettingsString = new StringComponent("UI Settings", this, true);
	private SliderComponent hueSlider;
	private CheckboxComponent rainbowBox;
	private SliderComponent effectSpeed;
	private CheckboxComponent armorHud;
	
	public OptionsTab(String title, int x, int y, SliderSetting hue, BooleanSetting rainbow, BooleanSetting ah, SliderSetting es) {
		super(title, x, y);
		this.setWidth(180);
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
