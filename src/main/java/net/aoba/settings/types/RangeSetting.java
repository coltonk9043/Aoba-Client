/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import net.aoba.settings.Setting;
import net.aoba.utils.types.Range;

public class RangeSetting extends Setting<Range> {
	public final float min_value;
	public final float max_value;
	public final float step;

	private RangeSetting(String ID, String displayName, String description, Range default_value, float min_value,
			float max_value, float step, Consumer<Range> onUpdate) {
		super(ID, displayName, description, default_value, onUpdate);
		this.min_value = min_value;
		this.max_value = max_value;
		this.step = step;
		type = TYPE.RANGE;
	}

	@Override
	public void setValue(Range value) {
		super.setValue(clamp(value));
	}

	@Override
	public void silentSetValue(Range value) {
		Range snapped = clamp(value);
		if (isValueValid(snapped))
			this.value = snapped;
	}

	@Override
	protected boolean isValueValid(Range value) {
		return value != null
				&& value.min() >= min_value && value.min() <= max_value
				&& value.max() >= min_value && value.max() <= max_value
				&& value.min() <= value.max();
	}

	/**
	 * Clamps a Range in between the values of this setting.
	 * @param range Range to clamp.
	 * @return Clamped ranged.
	 */
	private Range clamp(Range range) {
		float minValue = Math.max(min_value, Math.min(max_value, range.min()));
		float maxValue = Math.max(min_value, Math.min(max_value, range.max()));
		if (step > 0f) {
			minValue = min_value + Math.round((minValue - min_value) / step) * step;
			maxValue = min_value + Math.round((maxValue - min_value) / step) * step;
		}
		if (maxValue < minValue) 
			maxValue = minValue;
		return new Range(minValue, maxValue);
	}

	/**
	 * Returns a random value within the range.
	 * @return Random value between range as a float.
	 */
	public float randomValue() {
		Range r = getValue();
		float lo = r.min();
		float hi = r.max();
		if (hi <= lo)
			return lo;
		return ThreadLocalRandom.current().nextFloat() * (hi - lo) + lo;
	}
	
	public static RangeSetting.BUILDER builder() {
		return new RangeSetting.BUILDER();
	}

	public static class BUILDER extends Setting.BUILDER<RangeSetting.BUILDER, RangeSetting, Range> {
		protected float minValue = 0f;
		protected float maxValue = 100f;
		protected float step = 1f;

		protected BUILDER() {
		}

		public RangeSetting.BUILDER minValue(float value) {
			minValue = value;
			return this;
		}

		public RangeSetting.BUILDER maxValue(float value) {
			maxValue = value;
			return this;
		}

		public RangeSetting.BUILDER step(float value) {
			step = value;
			return this;
		}

		@Override
		public RangeSetting build() {
			return new RangeSetting(id, displayName, description, defaultValue, minValue, maxValue, step, onUpdate);
		}
	}
}
