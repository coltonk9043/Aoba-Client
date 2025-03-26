/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui;

import java.util.Objects;

public class Margin {
	// Dimensions - We want nullable so that we can determine that these dimensions
	// ARE NOT used.
	private Float left = null;
	private Float top = null;
	private Float right = null;
	private Float bottom = null;

	public Margin() {
	}

	public Margin(Float uniformMargin) {
		left = uniformMargin;
		top = uniformMargin;
		right = uniformMargin;
		bottom = uniformMargin;
	}

	public Margin(Float left, Float top, Float right, Float bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public Float getLeft() {
		return left;
	}

	public Float getTop() {
		return top;
	}

	public Float getRight() {
		return right;
	}

	public Float getBottom() {
		return bottom;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Margin otherMargin))
			return false;

        return Objects.equals(left, otherMargin.left) && Objects.equals(top, otherMargin.top)
				&& Objects.equals(right, otherMargin.right) && Objects.equals(bottom, otherMargin.bottom);
	}
}
