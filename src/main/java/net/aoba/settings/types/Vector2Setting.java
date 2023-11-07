package net.aoba.settings.types;

import net.aoba.core.utils.types.Vector2;
import net.aoba.settings.Setting;

import java.util.function.Consumer;

public class Vector2Setting extends Setting<Vector2> {
	
	public Vector2Setting(String ID, String description, Vector2 default_value) {
		super(ID, description, default_value);
		type = TYPE.VECTOR2;
	}
	
	public Vector2Setting(String ID, String displayName, String description, Vector2 default_value) {
		super(ID, displayName, description, default_value);
		type = TYPE.VECTOR2;
	}
	
	
	public Vector2Setting(String ID, String description, Vector2 default_value, Consumer<Vector2> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.VECTOR2;
	}

	/**
	 * Setter for the X coordinate of the Vector.
	 * @param x X Coordinate.
	 */
	public void setX(float x) {
		value.x = x;
		update();
	}

	/**
	 * Setter for the Y coordinate of the Vector.
	 * @param y Y Coordinate.
	 */
	public void setY(float y) {
		value.y = y;
		update();
	}

	/**
	 * Setter for the X coordinate of the Vector without calling the update function.
	 * @param x X Coordinate.
	 */
	public void silentSetX(float x) {
		value.x = x;
	}

	/**
	 * Setter for the Y coordinate of the Vector without calling the update function.
	 * @param y Y Coordinate.
	 */
	public void silentSetY(float y) {
		value.y = y;
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(Vector2 value) {
		return true;
	}
}
