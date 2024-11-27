package net.aoba.gui;

/**
 * A definition for a column or row in a Grid component. Defines the size and
 * the unit.
 */
public class GridDefinition {
	public enum RelativeUnit {
		Absolute, Relative, Auto
	}

	public final float value;
	public final RelativeUnit unit;

	public GridDefinition(float value) {
		this(value, RelativeUnit.Absolute);
	}

	public GridDefinition(float value, RelativeUnit unit) {
		this.value = value;
		this.unit = unit;
	}
}
