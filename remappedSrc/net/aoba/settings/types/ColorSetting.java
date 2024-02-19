package net.aoba.settings.types;

import net.aoba.gui.Color;
import net.aoba.settings.Setting;

public class ColorSetting extends Setting<Color> {

	public ColorSetting(String ID, String description, Color default_value) {
		super(ID, description, default_value);
		type = TYPE.COLOR;
	}

	public ColorSetting(String ID, String displayName, String description, Color default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.COLOR;
	}
	
	@Override
	protected boolean isValueValid(Color value) {
		return (value.r <=255 && value.g <= 255 && value.b <= 255);
	}
}
