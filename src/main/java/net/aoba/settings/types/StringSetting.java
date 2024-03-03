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

public class StringSetting extends Setting<String> {
	public StringSetting(String ID, String description, String default_value) {
		super(ID, description, default_value);
		type = TYPE.STRING;
	}

	public StringSetting(String ID, String displayName, String description, String default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.STRING;
	}

	public StringSetting(String ID, String description, String default_value, Consumer<String> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.STRING;
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(String value) {
		return true;
	}
}
