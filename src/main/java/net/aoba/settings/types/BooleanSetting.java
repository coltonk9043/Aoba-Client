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

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(String ID, String description, boolean default_value) {
		super(ID, description, default_value);
		type = TYPE.BOOLEAN;
	}
	
	public BooleanSetting(String ID, String displayName, String description, boolean default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.BOOLEAN;
	}
	
	public BooleanSetting(String ID, String displayName, String description, boolean default_value, Consumer<Boolean> onUpdate) {
		super(ID, displayName, description, default_value, onUpdate);
		type = TYPE.BOOLEAN;
	}
	
	public BooleanSetting(String ID, String description, boolean default_value, Consumer<Boolean> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.BOOLEAN;
	}

	/**
	 * Toggles the current value of the setting.
	 */
	public void toggle() {
		setValue(!value);
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(Boolean value) {
		return true;
	}
}
