/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.types;

public record Size(float width, float height) {
	public static final Size INFINITE = new Size(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
	public static final Size ZERO = new Size(0f, 0f);

	public Size withWidth(float width) {
		return new Size(width, this.height);
	}

	public Size withHeight(float height) {
		return new Size(this.width, height);
	}
}
