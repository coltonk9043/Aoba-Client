package net.aoba.core.settings.types;

import net.aoba.core.settings.Setting;
import java.util.function.Consumer;

public class FloatSetting extends Setting<Double> {
	public final double min_value;
	public final double max_value;
	public final double step;

	public FloatSetting(String ID, String description, double default_value, double min_value, double max_value, double step) {
		super(ID, description, default_value);
		this.min_value = min_value;
		this.max_value = max_value;
		this.step = step;
		type = TYPE.DOUBLE;
	}
	
	public FloatSetting(String ID, String displayName, String description, double default_value, double min_value, double max_value, double step) {
		super(ID, displayName, description, default_value);
		this.min_value = min_value;
		this.max_value = max_value;
		this.step = step;
		type = TYPE.DOUBLE;
	}
	
	public FloatSetting(String ID, String description, double default_value, double min_value, double max_value, double step, Consumer<Double> onUpdate) {
		super(ID, description, default_value, onUpdate);
		this.min_value = min_value;
		this.max_value = max_value;
		this.step = step;
		type = TYPE.DOUBLE;
	}

	/**
	 * Setter for the value. Includes rounding to the nearest "step".
	 */
	@Override
	public void setValue(Double value) {
		double newValue = Math.max(min_value, Math.min(max_value, value));
		int steps = (int) ((newValue - min_value) / step);
		super.setValue(step * steps);
	}
	
	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(Double value) {
		return value >= min_value && value <= max_value;
	}
}
