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
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;
import java.util.Objects;


public class Vec3dSetting extends Setting<Vec3d> {
    public final float min_value;
    public final float max_value;
    public final float step;

    public Vec3dSetting(String ID, String description, Vec3d default_value, float min_value, float max_value, float step) {
        super(ID, description, default_value);
        this.min_value = min_value;
        this.max_value = max_value;
        this.step = step;
        type = TYPE.VEC3D;
    }

    public Vec3dSetting(String ID, String displayName, String description, Vec3d default_value, float min_value, float max_value, float step) {
        super(ID, displayName, description, default_value);
        this.min_value = min_value;
        this.max_value = max_value;
        this.step = step;
        type = TYPE.VEC3D;
    }

    public Vec3dSetting(String ID, String description, Vec3d default_value, float min_value, float max_value, float step, Consumer<Vec3d> onUpdate) {
        super(ID, description, default_value, onUpdate);
        this.min_value = min_value;
        this.max_value = max_value;
        this.step = step;
        type = TYPE.VEC3D;
    }

    @Override
    public void setValue(Vec3d value) {
        float newX = (float) Math.max(min_value, Math.min(max_value, value.x));
        float newY = (float) Math.max(min_value, Math.min(max_value, value.y));
        float newZ = (float) Math.max(min_value, Math.min(max_value, value.z));

        int stepsX = Math.round(newX / step);
        int stepsY = Math.round(newY / step);
        int stepsZ = Math.round(newZ / step);

        super.setValue(new Vec3d(step * stepsX, step * stepsY, step * stepsZ));
    }

    @Override
    protected boolean isValueValid(Vec3d value) {
        return value.x >= min_value && value.x <= max_value &&
                value.y >= min_value && value.y <= max_value &&
                value.z >= min_value && value.z <= max_value;
    }
}
