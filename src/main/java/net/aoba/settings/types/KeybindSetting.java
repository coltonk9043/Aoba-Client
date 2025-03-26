/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import net.aoba.settings.Setting;
import net.minecraft.client.util.InputUtil.Key;
import java.util.function.Consumer;

public class KeybindSetting extends Setting<Key> {
    protected KeybindSetting(String ID, String displayName, String description, Key default_value, Consumer<Key> onUpdate) {
        super(ID, description, default_value, onUpdate);
        type = TYPE.KEYBIND;
    }

    /**
     * Checks whether or not a value is with this setting's valid range.
     */
    @Override
    protected boolean isValueValid(Key value) {
        return true;
    }
    
    public static BUILDER builder() {
    	return new BUILDER();
    }
    
    public static class BUILDER extends Setting.BUILDER<BUILDER, KeybindSetting, Key> {
		protected BUILDER() {
        }

		@Override
		public KeybindSetting build() {
			return new KeybindSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}