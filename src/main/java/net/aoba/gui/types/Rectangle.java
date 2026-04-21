/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.types;

public record Rectangle(float x, float y, float width, float height) {

	public boolean intersects(Rectangle rectangle) {
		return (Math.abs(x - rectangle.x) * 2 < (width + rectangle.width))
				&& (Math.abs(y - rectangle.y) * 2 < (height + rectangle.height));
	}

	public boolean intersects(float px, float py) {
		float x2 = x + width;
		float y2 = y + height;
		return (px >= x && px <= x2 && py >= y && py <= y2);
	}

	public Rectangle withX(float x) {
		return new Rectangle(x, this.y, this.width, this.height);
	}

	public Rectangle withY(float y) {
		return new Rectangle(this.x, y, this.width, this.height);
	}

	public Rectangle withWidth(float width) {
		return new Rectangle(this.x, this.y, width, this.height);
	}

	public Rectangle withHeight(float height) {
		return new Rectangle(this.x, this.y, this.width, height);
	}
}
