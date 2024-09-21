/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
			super();
		}

		@Override
		public KeybindSetting build() {
			return new KeybindSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}