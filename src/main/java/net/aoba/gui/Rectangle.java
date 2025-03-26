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

public class Rectangle {
	public static final Rectangle INFINITE = new Rectangle(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
			Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

	private Float x = null;
	private Float y = null;
	private Float width = null;
	private Float height = null;

	public Rectangle() {
	}

	public Rectangle(Float x, Float y, Float width, Float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle(Rectangle rect) {
		x = rect.x;
		y = rect.y;
		width = rect.width;
		height = rect.height;
	}

	@Nullable
	public Float getX() {
		return x;
	}

	@Nullable
	public Float getY() {
		return y;
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

		Rectangle otherRect = (Rectangle) other;

		if (!Objects.equals(x, otherRect.x))
			return false;
		if (!Objects.equals(y, otherRect.y))
			return false;
		if (!Objects.equals(width, otherRect.width))
			return false;
		return Objects.equals(height, otherRect.height);
	}

	public boolean intersects(Rectangle rectangle) {
		return (Math.abs(x - rectangle.x) * 2 < (width + rectangle.width))
				&& (Math.abs(y - rectangle.y) * 2 < (height + rectangle.height));
	}

	public boolean intersects(float x, float y) {
		float x2 = this.x + width;
		float y2 = this.y + height;

		return (x >= this.x && x <= x2 && y >= this.y && y <= y2);
	}

	public void setX(Float x) {
		this.x = x;
	}

	public void setY(Float y) {
		this.y = y;
	}

	public void setWidth(Float width) {
		this.width = width;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	/**
	 * Returns whether or not this rectangle can be used for rendering, such that
	 * the X, Y, Width, and Height dimensions are all non-null.
	 *
	 * @return Whether this rectangle can be used for rendering.
	 */
	public boolean isDrawable() {
		return !(x == null || y == null || width == null || height == null);
	}
}
