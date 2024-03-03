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

/**
 * A class to represent a Color that iterates.
 */
package net.aoba.misc;

import net.aoba.gui.Color;

public class RainbowColor {
	private Color color;
	private float timer = 0f;

	public RainbowColor() {
		this.color = new Color(255, 0, 0);
	}

	public void update(float timerIncrement) {
		if (timer >= (20 - timerIncrement)) {
			timer = 0f;
			this.color.setHSV(((this.color.hue + 1f) % 361), 1f, 1f);
		} else {
			timer++;
		}

	}

	public Color getColor() {
		return this.color;
	}
}
