package net.aoba.settings.types;

import java.util.function.Consumer;

import net.aoba.settings.Setting;

public class IntegerSetting extends Setting<Integer> {
	public final int min_value;
	public final int max_value;
	public final int step;

	public IntegerSetting(String ID, String description, Integer default_value, int min_value, int max_value,
			int step) {
		super(ID, description, default_value);
		this.min_value = min_value;
		this.max_value = max_value;
		this.step = step;
		type = TYPE.INTEGER;
	}

	public IntegerSetting(String ID, String displayName, String description, Integer default_value, int min_value, int max_value, int step) {
		super(ID, displayName, description, default_value);
		this.min_value = min_value;
		this.max_value = max_value;
		this.step = step;
		type = TYPE.INTEGER;
	}

	public IntegerSetting(String ID, String description, int default_value, int min_value, int max_value, int step,
			Consumer<Integer> onUpdate) {
		super(ID, description, default_value, onUpdate);
		this.min_value = min_value;
		this.max_value = max_value;
		this.step = step;
		type = TYPE.INTEGER;
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(Integer value) {
		return value >= min_value && value <= max_value;
	}
}
