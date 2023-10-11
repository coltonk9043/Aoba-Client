package net.aoba.core.settings.types;

import java.util.function.Consumer;
import net.aoba.core.settings.Setting;
import net.minecraft.client.option.KeyBinding;

public class KeybindSetting extends Setting<KeyBinding> {
	public KeybindSetting(String ID, String description, KeyBinding default_value) {
		super(ID, description, default_value);
		type = TYPE.KEYBIND;
	}

	public KeybindSetting(String ID, String displayName, String description, KeyBinding default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.KEYBIND;
	}

	public KeybindSetting(String ID, String description, KeyBinding default_value, Consumer<KeyBinding> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.KEYBIND;
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(KeyBinding value) {
		return true;
	}
}