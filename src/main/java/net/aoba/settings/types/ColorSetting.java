/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import java.util.function.Consumer;

import net.aoba.gui.colors.Color;
import net.aoba.settings.Setting;

public class ColorSetting extends Setting<Color> {

    protected ColorSetting(String ID, String displayName, String description, Color default_value, Consumer<Color> onUpdate) {
        super(ID, displayName, description, default_value);
        type = TYPE.COLOR;
    }

    @Override
    protected boolean isValueValid(Color value) {
        return value != null;
    }

    public static ColorSetting.BUILDER builder() {
    	return new ColorSetting.BUILDER();
    }

    public static class BUILDER extends Setting.BUILDER<ColorSetting.BUILDER, ColorSetting, Color> {
		protected BUILDER() {
        }

		@Override
		public ColorSetting build() {
			return new ColorSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
