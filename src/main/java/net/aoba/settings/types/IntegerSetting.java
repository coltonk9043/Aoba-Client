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
