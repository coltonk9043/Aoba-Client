/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import net.aoba.settings.Setting;
import java.util.function.Consumer;

public class IntegerSetting extends Setting<Integer> {
    public final int min_value;
    public final int max_value;
    public final int step;

    protected IntegerSetting(String ID, String displayName, String description, int default_value, int min_value, int max_value, int step, Consumer<Integer> onUpdate) {
        super(ID, displayName, description, default_value, onUpdate);
        this.min_value = min_value;
        this.max_value = max_value;
        this.step = step;
        type = TYPE.INTEGER;
    }

    /**
     * Checks whether or not a value is with this setting's valid range.
     */
    @Override
    protected boolean isValueValid(Integer value) {
        return value >= min_value && value <= max_value;
    }
    
    public static BUILDER builder() {
    	return new BUILDER();
    }
    
    public static class BUILDER extends Setting.BUILDER<BUILDER, IntegerSetting, Integer> {
		protected Integer minValue = 1;
		protected Integer maxValue = 10;
		protected Integer step = 1;
		
		protected BUILDER() {
        }
		
		public BUILDER minValue(Integer value) {
			minValue = value;
			return this;
		}
		
		public BUILDER maxValue(Integer value) {
			maxValue = value;
			return this;
		}
		
		public BUILDER step(Integer value) {
			step = value;
			return this;
		}
		
		@Override
		public IntegerSetting build() {
			return new IntegerSetting(id, displayName, description, defaultValue, minValue, maxValue, step, onUpdate);
		}
	}
}
