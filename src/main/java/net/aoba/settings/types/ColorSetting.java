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

import net.aoba.gui.GuiManager;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.ColorMode;
import net.aoba.settings.Setting;

public class ColorSetting extends Setting<Color> {

    private ColorMode mode;

    public ColorSetting(String ID, String description, Color default_value) {
        super(ID, description, default_value);
        type = TYPE.COLOR;
    }

    public ColorSetting(String ID, String displayName, String description, Color default_value) {
        super(ID, displayName, description, default_value);
        type = TYPE.COLOR;
    }

    @Override
    protected boolean isValueValid(Color value) {
        return (value.r <= 255 && value.g <= 255 && value.b <= 255);
    }

    public void setMode(ColorMode color) {
        mode = color;
        switch (mode) {
            case Normal:
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
}
