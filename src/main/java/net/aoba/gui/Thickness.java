/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui;

public record Thickness(Float left, Float top, Float right, Float bottom) {
	public Thickness() {
		this(null, null, null, null);
	}

	public Thickness(Float uniformMargin) {
		this(uniformMargin, uniformMargin, uniformMargin, uniformMargin);
	}
}
