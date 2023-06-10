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
 * An abstract setting.
 */
package net.aoba.settings;

public abstract class Setting {
	private final String name;
	private final String line;
	
	public Setting(String name, String line) {
		this.name = name;
		this.line = line;
	}
	
	/**
	 * Returns the name of the setting.
	 * @return The name of the setting.
	 */
	public final String getName() {
		return this.name;
	}
	
	/**
	 * Returns the codename of the setting.
	 * @return The codename of the setting.
	 */
	public final String getLine() {
		return this.line;
	}
	
	/**
	 * Abstract function to load the value of a given setting.
	 */
	public abstract void loadSetting();
}
