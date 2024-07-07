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
     *
     * @return Index of the setting.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Getter for the current value stored at a specific index of the setting.
     *
     * @return Value at the current index of the setting.
     */
    public String getIndexValue() {
        return value.get(index);
    }

    /**
     * Setter for the current index of the setting.
     *
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
