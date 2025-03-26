/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import net.aoba.settings.Setting;
import net.minecraft.util.math.Vec3d;
import java.util.function.Consumer;

import org.joml.Vector2d;

public class Vec3dSetting extends Setting<Vec3d> {
    public final float min_value;
    public final float max_value;
    public final float step;

    protected Vec3dSetting(String ID, String displayName, String description, Vec3d default_value, float min_value, float max_value, float step, Consumer<Vec3d> onUpdate) {
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
    
    public static BUILDER builder() {
    	return new BUILDER();
    }
    
    public static class BUILDER extends Setting.BUILDER<BUILDER, Vec3dSetting, Vec3d> {
		protected Float minValue = 1f;
		protected Float maxValue = 10f;
		protected Float step = 1f;
		
		protected BUILDER() {
        }
		
		public BUILDER minValue(Float value) {
			minValue = value;
			return this;
		}
		
		public BUILDER maxValue(Float value) {
			maxValue = value;
			return this;
		}
		
		public BUILDER step(Float value) {
			step = value;
			return this;
		}
		
		@Override
		public Vec3dSetting build() {
			return new Vec3dSetting(id, displayName, description, defaultValue, minValue, maxValue, step, onUpdate);
		}
	}
}
