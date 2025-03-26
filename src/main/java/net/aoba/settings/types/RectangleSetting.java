/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import java.util.function.Consumer;

import net.aoba.gui.Rectangle;
import net.aoba.settings.Setting;

public class RectangleSetting extends Setting<Rectangle> {
	protected RectangleSetting(String ID, String displayName, String description, Rectangle default_value,
			Consumer<Rectangle> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.RECTANGLE;
	}

	/**
	 * Setter for the Rectangle size.
	 *
	 * @param size Size of the Rectangle.
	 */
	public void setSize(Rectangle size) {
		if (!value.equals(size)) {
			value = size;
			update();
		}
	}

	/**
	 * Setter for the Rectangle X location.
	 *
	 * @param x X Coordinate.
	 */
	public void setX(Float x) {
		if (!value.getX().equals(x)) {
			Rectangle oldRect = value;
			value = new Rectangle(x, oldRect.getY(), oldRect.getWidth(), oldRect.getHeight());
			update();
		}
	}

	/**
	 * Setter for the Rectangle Y location.
	 *
	 * @param y Y Coordinate.
	 */
	public void setY(Float y) {
		if (!value.getY().equals(y)) {
			Rectangle oldRect = value;
			value = new Rectangle(oldRect.getX(), y, oldRect.getWidth(), oldRect.getHeight());
			update();
		}
	}

	/**
	 * Getter for the Rectangle X location.
	 *
	 * @return X Coordinate.
	 */
	public Float getX() {
		return value.getX();
	}

	/**
	 * Getter for the Rectangle Y location.
	 *
	 * @return Y Coordinate.
	 */
	public Float getY() {
		return value.getY();
	}

	/**
	 * Getter for the Rectangle width.
	 *
	 * @return Width Coordinate.
	 */
	public Float getWidth() {
		return value.getWidth();
	}

	/**
	 * Getter for the Rectangle height.
	 *
	 * @return Height Coordinate.
	 */
	public Float getHeight() {
		return value.getHeight();
	}

	/**
	 * Setter for the Rectangle width.
	 *
	 * @param width Width Coordinate.
	 */
	public void setWidth(Float width) {
		Float currentWidth = value.getWidth();
		if (currentWidth == null || !currentWidth.equals(width)) {
			value.setWidth(width);
			update();
		}
	}

	/**
	 * Setter for the Rectangle height.
	 *
	 * @param height Height Coordinate.
	 */
	public void setHeight(Float height) {
		Float currentHeight = value.getHeight();
		if (currentHeight == null || !currentHeight.equals(height)) {
			value.setHeight(height);
			update();
		}
	}

	/**
	 * Setter for the Rectangle size without calling the update function.
	 *
	 * @param size Size Coordinate.
	 */
	public void silentSetSize(Rectangle size) {
		value = size;
	}

	/**
	 * Checks whether a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(Rectangle value) {
		return true;
	}

	public static BUILDER builder() {
		return new BUILDER();
	}

	public static class BUILDER extends Setting.BUILDER<BUILDER, RectangleSetting, Rectangle> {
		protected BUILDER() {
        }

		@Override
		public RectangleSetting build() {
			return new RectangleSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
