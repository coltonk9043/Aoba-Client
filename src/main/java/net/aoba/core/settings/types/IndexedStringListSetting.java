package net.aoba.core.settings.types;

import java.util.List;
import java.util.function.Consumer;

public class IndexedStringListSetting extends StringListSetting {
	protected int index = 0;

	public IndexedStringListSetting(String ID, String description, List<String> default_value) {
		super(ID, description, default_value);
		type = TYPE.INDEXEDSTRINGLIST;
	}
	
	public IndexedStringListSetting(String ID, String displayName, String description, List<String> default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.INDEXEDSTRINGLIST;
	}
	
	public IndexedStringListSetting(String ID, String description, List<String> default_value,
			Consumer<List<String>> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.INDEXEDSTRINGLIST;
	}

	/**
	 * Getter for the current index of the setting.
	 * @return Index of the setting.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Getter for the current value stored at a specific index of the setting.
	 * @return Value at the current index of the setting.
	 */ 
	public String getIndexValue() {
		return value.get(index);
	}

	/**
	 * Setter for the current index of the setting.
	 * @param newValue The new index to set the current index to.
	 */
	public void setIndex(int newValue) {
		if (index > value.size()) 
			index = value.size() - 1;
		if (index < 0) 
			index = 0;
		index = newValue;
	}
	
	/**
	 * Increments the current index of the setter.
	 */
	public void increment() {
		index += 1;
		if (index > value.size()) {
			index = 0;
		}
	}

	/**
	 * Decrements the current index of the setter.
	 */
	public void decrement() {
		index -= 1;
		if (index < 0) {
			index = value.size() - 1;
		}
	}
}
