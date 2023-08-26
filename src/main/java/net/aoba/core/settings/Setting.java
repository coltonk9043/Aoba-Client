package net.aoba.core.settings;

import java.util.function.Consumer;

import net.aoba.misc.TextUtils;

public abstract class Setting<T> {
	
	public enum TYPE {
		BOOLEAN, DOUBLE, STRING, INTEGER, STRINGLIST, INDEXEDSTRINGLIST, VECTOR2
	}
	
	public final String ID;
	public final String displayName;
	public final String description;
	protected final T default_value;

	protected T value;

	public TYPE type;

	// Consumers
	private final Consumer<T> onUpdate;

	public Setting(String ID, String description, T default_value) {
		this.ID = ID;
		this.displayName = TextUtils.IDToName(ID);
		this.description = description;
		this.default_value = default_value;
		this.onUpdate = null;
		this.value = default_value;
	}
	
	public Setting(String ID, String displayName, String description, T default_value) {
		this.ID = ID;
		this.displayName = displayName;
		this.description = description;
		this.default_value = default_value;
		this.onUpdate = null;
		this.value = default_value;
	}

	public Setting(String ID, String description, T default_value, Consumer<T> onUpdate) {
		this.ID = ID;
		this.displayName = TextUtils.IDToName(ID);
		this.description = description;
		this.default_value = default_value;
		this.onUpdate = onUpdate;
		this.value = default_value;
	}

	/**
	 * Getter for the current value.
	 * @return The value currently stored in the Setting.
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Setter for the current value.
	 * @param value The value to set.
	 */
	public void setValue(T value) {
		if (isValueValid(value)) {
			this.value = value;
		}
		update();
	}

	/**
	 * Setter for the current value that does not call update.
	 * @param value The value to set.
	 */
	public void silentSetValue(T value) {
		if (isValueValid(value)) {
			this.value = value;
		}
	}

	/**
	 * Resets the value of the setting to the default value.
	 */
	public void resetValue() {
		setValue(default_value);
	}

	/**
	 * Function that handles when the value is updated.
	 */
	public void update() {
		if (onUpdate != null) {
			onUpdate.accept(value);
		}
	}

	/**
	 * Getter for the default value.
	 * @return
	 */
	public T getDefaultValue() {
		return default_value;
	}

	/**
	 * Function that will check if a value is valid for this particular setting.
	 * @param value The value to test.
	 * @return True if the value is valid.
	 */
	protected abstract boolean isValueValid(T value);
}
