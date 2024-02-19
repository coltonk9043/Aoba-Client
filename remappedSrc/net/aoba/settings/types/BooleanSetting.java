package net.aoba.settings.types;

import java.util.function.Consumer;

import net.aoba.settings.Setting;

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(String ID, String description, boolean default_value) {
		super(ID, description, default_value);
		type = TYPE.BOOLEAN;
	}
	
	public BooleanSetting(String ID, String displayName, String description, boolean default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.BOOLEAN;
	}
	
	public BooleanSetting(String ID, String description, boolean default_value, Consumer<Boolean> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.BOOLEAN;
	}

	/**
	 * Toggles the current value of the setting.
	 */
	public void toggle() {
		setValue(!value);
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(Boolean value) {
		return true;
	}
}
