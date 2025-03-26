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

public class BooleanSetting extends Setting<Boolean> {
    protected BooleanSetting(String ID, String displayName, String description, boolean default_value, Consumer<Boolean> onUpdate) {
        super(ID, displayName, description, default_value, onUpdate);
        type = TYPE.BOOLEAN;
    }

    /**
     * Toggles the current value of the setting.
     */
    public void toggle() {
        setValue(!value);
    }

    /**
     * Checks whether or not a value is with this setting's valid range.
     */
    @Override
    protected boolean isValueValid(Boolean value) {
        return true;
    }
    
    public static BUILDER builder() {
    	return new BUILDER();
    }
    
    public static class BUILDER extends Setting.BUILDER<BUILDER, BooleanSetting, Boolean> {
		protected BUILDER() {
        }
		
		@Override
		public BooleanSetting build() {
			return new BooleanSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
