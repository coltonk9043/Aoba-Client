package net.aoba.core.settings.types;

import net.aoba.core.settings.Setting;
import java.util.function.Consumer;

public class StringSetting extends Setting<String> {
	public StringSetting(String ID, String description, String default_value) {
		super(ID, description, default_value);
		type = TYPE.STRING;
	}

	public StringSetting(String ID, String displayName, String description, String default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.STRING;
	}

	public StringSetting(String ID, String description, String default_value, Consumer<String> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.STRING;
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(String value) {
		return true;
	}
}
