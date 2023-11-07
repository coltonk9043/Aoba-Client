package net.aoba.settings.types;

import java.util.function.Consumer;

import net.aoba.settings.Setting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

public class KeybindSetting extends Setting<Key> {
	public KeybindSetting(String ID, String description, Key default_value) {
		super(ID, description, default_value);
		type = TYPE.KEYBIND;
	}

	public KeybindSetting(String ID, String displayName, String description, Key default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.KEYBIND;
	}

	public KeybindSetting(String ID, String description, Key default_value, Consumer<Key> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.KEYBIND;
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(Key value) {
		return true;
	}
}