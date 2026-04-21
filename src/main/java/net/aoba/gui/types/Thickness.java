/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.types;

public record Thickness(float left, float top, float right, float bottom) {
	public Thickness() {
		this(0f, 0f, 0f, 0f);
	}

	public Thickness(float uniform) {
		this(uniform, uniform, uniform, uniform);
	}

	public Thickness(float horizontal, float vertical) {
		this(horizontal, vertical, horizontal, vertical);
	}

	public float horizontalSum() { return left + right; }
	public float verticalSum()   { return top + bottom; }
}
