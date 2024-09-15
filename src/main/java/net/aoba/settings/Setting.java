/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings;

import net.aoba.utils.render.TextUtils;

import java.util.HashSet;
import java.util.function.Consumer;

public abstract class Setting<T> {

    public enum TYPE {
        BOOLEAN, FLOAT, STRING, INTEGER, STRINGLIST, INDEXEDSTRINGLIST, VECTOR2, KEYBIND, COLOR, BLOCKS, ENUM, RECTANGLE, VEC3D
    }

    public final String ID;
    public final String displayName;
    public final String description;
    protected final T default_value;

    protected T value;

    public TYPE type;

    // Consumers
    private HashSet<Consumer<T>> onUpdate = new HashSet<Consumer<T>>();

    public Setting(String ID, String description, T default_value) {
        this.ID = ID;
        this.displayName = TextUtils.IDToName(ID);
        this.description = description;
        this.default_value = default_value;
        this.value = default_value;
    }

    public Setting(String ID, String displayName, String description, T default_value) {
        this.ID = ID;
        this.displayName = displayName;
        this.description = description;
        this.default_value = default_value;
        this.value = default_value;
    }

    public Setting(String ID, String displayName, String description, T default_value, Consumer<T> onUpdate) {
        this.ID = ID;
        this.displayName = displayName;
        this.description = description;
        this.default_value = default_value;
        this.onUpdate.add(onUpdate);
        this.value = default_value;
    }

    public Setting(String ID, String description, T default_value, Consumer<T> onUpdate) {
        this.ID = ID;
        this.displayName = TextUtils.IDToName(ID);
        this.description = description;
        this.default_value = default_value;
        this.onUpdate.add(onUpdate);
        this.value = default_value;
    }

    /**
     * Getter for the current value.
     *
     * @return The value currently stored in the Setting.
     */
    public T getValue() {
        return value;
    }

    /**
     * Setter for the current value.
     *
     * @param value The value to set.
     */
    public void setValue(T value) {
        if (isValueValid(value)) {
            this.value = value;
        }
        update();
    }

    /**
     * Resets the value of the setting to the default value.
     */
    public void resetToDefault() {
        resetValue();
        update();
    }

    /**
     * Setter for the current value that does not call update.
     *
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
        	for(Consumer<T> consumer : onUpdate) {
        		consumer.accept(value);
        	}
        }
    }

    public void addOnUpdate(Consumer<T> consumer) {
        this.onUpdate.add(consumer);
    }
    
    public void removeOnUpdate(Consumer<T> consumer) {
    	this.onUpdate.add(consumer);
    }

    /**
     * Getter for the default value.
     *
     * @return
     */
    public T getDefaultValue() {
        return default_value;
    }

    /**
     * Function that will check if a value is valid for this particular setting.
     *
     * @param value The value to test.
     * @return True if the value is valid.
     */
    protected abstract boolean isValueValid(T value);
}
