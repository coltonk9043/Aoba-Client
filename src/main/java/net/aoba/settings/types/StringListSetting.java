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

package net.aoba.settings.types;

import net.aoba.settings.Setting;

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
     *
     * @param value String to add to the list.
     */
    public void appendString(String value) {
        this.value.add(value);
        update();
    }

    /**
     * Removes a string at an index from the list.
     *
     * @param index Index to remove the string from.
     */
    public void removeAtIndex(int index) {
        if (index >= 0 && index < value.size()) {
            value.remove(index);
            update();
        }
    }

    /**
     * Removes a string from the list
     *
     * @param newValue The value to remove from the array.
     */
    public void removeString(String newValue) {
        for (String s : value) {
            if (s.equals(newValue)) {
                value.remove(s);
                update();
                break;
            }
        }
    }

    /**
     * Getter for a String at a specific index.
     *
     * @param index Index to fetch the string from.
     * @return String in the list at index.
     */
    public String getValueAt(int index) {
        if (index >= 0 && index < value.size()) {
            return value.get(index);
        }
        return null;
    }

    /**
     * Checks whether or not a value is with this setting's valid range.
     */
    @Override
    protected boolean isValueValid(List<String> value) {
        return true;
    }
}
