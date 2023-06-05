/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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

/**
 * An setting that holds a variable of true or false.
 */
package net.aoba.settings;

public class BooleanSetting extends Setting{

	private boolean value;
	private final boolean defaultValue;
	
	public BooleanSetting(String name, String line) {
		super(name, line);
		this.defaultValue = false;
		this.loadSetting();
	}

	public final boolean getValue() {
		return this.value;
	}
	
	public final void setValue(boolean value) {
		this.value = value;
	}
	
	public final void toggleValue() {
		this.value = !value;
	}

	@Override
	public void loadSetting() {
		try {
			this.value = Settings.getSettingBoolean(this.getLine());
		}catch(Exception e) {
			e.printStackTrace();
			this.value = this.defaultValue;
		}
	}
}
