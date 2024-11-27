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
		this.left = uniformMargin;
		this.top = uniformMargin;
		this.right = uniformMargin;
		this.bottom = uniformMargin;
	}

	public Margin(Float left, Float top, Float right, Float bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public Float getLeft() {
		return this.left;
	}

	public Float getTop() {
		return this.top;
	}

	public Float getRight() {
		return this.right;
	}

	public Float getBottom() {
		return this.bottom;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Margin))
			return false;

		Margin otherMargin = (Margin) other;
		return Objects.equals(left, otherMargin.left) && Objects.equals(top, otherMargin.top)
				&& Objects.equals(right, otherMargin.right) && Objects.equals(bottom, otherMargin.bottom);
	}
}
