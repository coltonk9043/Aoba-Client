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
                this.setValue(default_value);
                break;
            case Rainbow:
                this.setValue(GuiManager.rainbowColor);
                break;
            case Random:
                this.setValue(GuiManager.randomColor);
                break;
        }
    }
    
    public static BUILDER builder() {
    	return new BUILDER();
    }
    
    public static class BUILDER extends Setting.BUILDER<BUILDER, ColorSetting, Color> {
		protected BUILDER() {
			super();
		}
		
		@Override
		public ColorSetting build() {
			return new ColorSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
