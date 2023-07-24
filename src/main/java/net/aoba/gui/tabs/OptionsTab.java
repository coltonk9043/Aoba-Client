package net.aoba.gui.tabs;

import net.aoba.core.osettings.osettingtypes.BooleanOSetting;
import net.aoba.core.osettings.osettingtypes.DoubleOSetting;
import net.aoba.gui.tabs.components.CheckboxComponent;
import net.aoba.gui.tabs.components.SliderComponent;
import net.aoba.gui.tabs.components.StringComponent;

public class OptionsTab extends ClickGuiTab {

	private StringComponent uiSettingsString = new StringComponent("UI Settings", this, true);
	private SliderComponent hueSlider;
	private CheckboxComponent rainbowBox;
	private SliderComponent effectSpeed;
	private CheckboxComponent armorHud;
	
	public OptionsTab(String title, int x, int y, DoubleOSetting hue, BooleanOSetting rainbow, BooleanOSetting ah, DoubleOSetting es) {
		super(title, x, y, false);
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
