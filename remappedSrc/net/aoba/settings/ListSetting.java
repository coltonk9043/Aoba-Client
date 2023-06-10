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
 * A setting that represents a list of choices.
 */
package net.aoba.settings;

public class ListSetting extends Setting {

	private String value;
	private int index;
	private final String defaultValue;
	private String[] options;

	public ListSetting(String name, String line, String[] options) {
		super(name, line);
		this.options = options;
		this.defaultValue = options[0];
		this.value = options[0];
		this.index = 0;
		// this.loadSetting();
	}

	public final String getValue() {
		return this.value;
	}

	public final void setValue(String value) {
		this.value = value;
	}

	public void increment() {
		index++;
		if (index >= this.options.length) {
			index = 0;
		}
		this.value = this.options[index];
	}

	public void decrement() {
		index--;
		if (index < 0) {
			this.index = this.options.length - 1;
		}
		this.value = this.options[index];
	}

	@Override
	public void loadSetting() {
		try {
			this.value = Settings.getSettingString(this.getLine());
		} catch (Exception e) {
			e.printStackTrace();
			this.value = this.defaultValue;
		}
	}
}
