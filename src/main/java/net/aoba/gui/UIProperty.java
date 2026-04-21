/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui;

public class UIProperty<T> {
	@FunctionalInterface
	public interface PropertyChangedCallback<T> {
		void changed(UIElement sender, T oldValue, T newValue);
	}

	@FunctionalInterface
	public interface CoerseCallback<T> {
		T coerce(UIElement sender, T newValue);
	}
	
	private final String name;
	private final T defaultValue;
	private final boolean inherits;
	private final boolean affectsLayout;
	private final PropertyChangedCallback<T> changedCallback;
	private final CoerseCallback<T> coerceCallback;

	public UIProperty(String name, T defaultValue) {
		this(name, defaultValue, false);
	}

	public UIProperty(String name, T defaultValue, boolean inherits) {
		this(name, defaultValue, inherits, false, null, null);
	}

	public UIProperty(String name, T defaultValue, boolean inherits, boolean affectsLayout) {
		this(name, defaultValue, inherits, affectsLayout, null, null);
	}

	public UIProperty(String name, T defaultValue, boolean inherits, boolean affectsLayout, PropertyChangedCallback<T> changedCallback) {
		this(name, defaultValue, inherits, affectsLayout, changedCallback, null);
	}
	

	public UIProperty(String name, T defaultValue, boolean inherits, boolean affectsLayout,
			PropertyChangedCallback<T> changedCallback, CoerseCallback<T> coerce) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.inherits = inherits;
		this.affectsLayout = affectsLayout;
		this.changedCallback = changedCallback;
		this.coerceCallback = coerce;
	}

	public String getName() {
		return name;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public boolean inherits() {
		return inherits;
	}

	public boolean affectsLayout() {
		return affectsLayout;
	}

	public PropertyChangedCallback<T> getChangedCallback() {
		return changedCallback;
	}

	public CoerseCallback<T> getCoerceCallback() {
		return coerceCallback;
	}
}
