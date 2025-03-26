/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import java.util.function.Consumer;

import net.aoba.gui.GuiManager;
import net.aoba.gui.colors.Color;
import net.aoba.settings.Setting;
import net.aoba.settings.types.BooleanSetting.BUILDER;

public class ColorSetting extends Setting<Color> {
	public enum ColorMode {
	    Solid,
	    Rainbow,
	    Random,
	}

    private ColorMode mode = ColorMode.Solid;

    protected ColorSetting(String ID, String displayName, String description, Color default_value, Consumer<Color> onUpdate) {
        super(ID, displayName, description, default_value);
        type = TYPE.COLOR;
    }

    @Override
    protected boolean isValueValid(Color value) {
        return (value.getRed() <= 255 && value.getGreen() <= 255 && value.getBlue() <= 255);
    }

    public ColorMode getMode() {
    	return mode;
    }
    
    public void setMode(ColorMode color) {
        mode = color;
        switch (mode) {
            case Solid:
                setValue(default_value);
                break;
            case Rainbow:
                setValue(GuiManager.rainbowColor);
                break;
            case Random:
                setValue(GuiManager.randomColor);
                break;
        }
    }
    
    public static BUILDER builder() {
    	return new BUILDER();
    }
    
    public static class BUILDER extends Setting.BUILDER<BUILDER, ColorSetting, Color> {
		protected BUILDER() {
        }
		
		@Override
		public ColorSetting build() {
			return new ColorSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
