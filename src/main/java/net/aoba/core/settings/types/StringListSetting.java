package net.aoba.core.settings.types;

import net.aoba.core.settings.Setting;

import java.util.List;
import java.util.function.Consumer;

public class StringListSetting extends Setting<List<String>> {
	public StringListSetting(String ID, String description, List<String> default_value) {
		super(ID, description, default_value);
		type = TYPE.STRINGLIST;
	}
	
	public StringListSetting(String ID, String displayName, String description, List<String> default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.STRINGLIST;
	}
	
	public StringListSetting(String ID, String description, List<String> default_value,
			Consumer<List<String>> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.STRINGLIST;
	}

	/**
	 * Appends a new string to the list.
	 * @param value String to add to the list.
	 */
	public void appendString(String value) {
		this.value.add(value);
		update();
	}

	/**
	 * Removes a string at an index from the list.
	 * @param index Index to remove the string from.
	 */
	public void removeAtIndex(int index) {
		if (index >= 0 && index < value.size()) {
			value.remove(index);
			update();
		}
		// TODO: add out of bounds error .. maybe
	}
	
	/**
	 * Removes a string from the list
	 * @param newValue The value to remove from the array.
	 */
	public void removeString(String newValue) {
		for(String s : value) {
			if(s.equals(newValue)) {
				value.remove(s);
				update();
				break;
			}
		}
	}

	/**
	 * Getter for a String at a specific index.
	 * @param index Index to fetch the string from.
	 * @return String in the list at index.
	 */
	public String getValueAt(int index) {
		if (index >= 0 && index < value.size()) {
			return value.get(index);
		}
		return null;
		// TODO: add out of bounds error .. maybe
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(List<String> value) {
		return true;
	}
}
