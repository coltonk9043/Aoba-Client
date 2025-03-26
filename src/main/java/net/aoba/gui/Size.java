/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

public class Size {
	public static final Size INFINITE = new Size(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
	public static final Size ZERO = new Size(0f, 0f);
	private Float width = null;
	private Float height = null;

	public Size() {
	}

	public Size(Float width, Float height) {
		this.width = width;
		this.height = height;
	}

	@Nullable
	public Float getWidth() {
		return width;
	}

	@Nullable
	public Float getHeight() {
		return height;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null || getClass() != other.getClass())
			return false;

		Size otherRect = (Size) other;

		if (!Objects.equals(width, otherRect.width))
			return false;
		return Objects.equals(height, otherRect.height);
	}

	public void setWidth(Float width) {
		this.width = width;
	}

	public void setHeight(Float height) {
		this.height = height;
	}
}
