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
 * A class to represent a setting related to a Slider.
 */
package net.aoba.settings;

public class SliderSetting extends Setting{
	
	private double value;
	private final double defaultValue;
	private final double minValue;
	private final double maxValue;
	private final double increment;
	
	public SliderSetting(String name, String line, double value, double min, double max, double increment) {
		super(name, line);
		this.value = value;
		this.defaultValue = value;
		this.minValue = min;
		this.maxValue	=max;
		this.increment = increment;
	}
	
	public final double getValue() {
		return this.value;
	}
	
	public final float getValueFloat() {
		return (float)this.value;
	}
	
	public final int getValueInt() {
		return (int)this.value;
	}
	
	public final void setValue(double value)
	{
		this.value = this.increment*(Math.ceil(Math.abs(value/this.increment)));;
	}
	
	public final double getMinimum()
	{
		return this.minValue;
	}
	
	public final double getMaximum()
	{
		return this.minValue;
	}
	
	public final double getIncrement()
	{
		return increment;
	}
	
	public final double getRange()
	{
		return this.maxValue - this.minValue;
	}
	
	@Override
	public void loadSetting() {
		try {
			this.value = Settings.getSettingFloat(this.getLine());
		}catch(Exception e) {
			e.printStackTrace();
			this.value = this.defaultValue;
		}
	}
}
