/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import java.util.function.Consumer;

import net.aoba.gui.types.Rectangle;
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
	public void setX(float x) {
		if (value.x() != x) {
			value = new Rectangle(x, value.y(), value.width(), value.height());
			update();
		}
	}

	/**
	 * Setter for the Rectangle Y location.
	 *
	 * @param y Y Coordinate.
	 */
	public void setY(float y) {
		if (value.y() != y) {
			value = new Rectangle(value.x(), y, value.width(), value.height());
			update();
		}
	}

	/**
	 * Getter for the Rectangle X location.
	 *
	 * @return X Coordinate.
	 */
	public float getX() {
		return value.x();
	}

	/**
	 * Getter for the Rectangle Y location.
	 *
	 * @return Y Coordinate.
	 */
	public float getY() {
		return value.y();
	}

	/**
	 * Getter for the Rectangle width.
	 *
	 * @return Width Coordinate.
	 */
	public float getWidth() {
		return value.width();
	}

	/**
	 * Getter for the Rectangle height.
	 *
	 * @return Height Coordinate.
	 */
	public float getHeight() {
		return value.height();
	}

	/**
	 * Setter for the Rectangle width.
	 *
	 * @param width Width Coordinate.
	 */
	public void setWidth(float width) {
		if (value.width() != width) {
			value = new Rectangle(value.x(), value.y(), width, value.height());
			update();
		}
	}

	/**
	 * Setter for the Rectangle height.
	 *
	 * @param height Height Coordinate.
	 */
	public void setHeight(float height) {
		if (value.height() != height) {
			value = new Rectangle(value.x(), value.y(), value.width(), height);
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

	public static RectangleSetting.BUILDER builder() {
		return new RectangleSetting.BUILDER();
	}

	public static class BUILDER extends Setting.BUILDER<RectangleSetting.BUILDER, RectangleSetting, Rectangle> {
		protected BUILDER() {
        }

		@Override
		public RectangleSetting build() {
			return new RectangleSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
