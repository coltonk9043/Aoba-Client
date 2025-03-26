/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.rotation.goals;

import net.aoba.managers.rotation.Rotation;
import net.aoba.managers.rotation.RotationMode;
import net.minecraft.util.math.Vec3d;

public class Vec3dGoal extends Goal<Vec3d> {

	@Override
	public Rotation getGoalRotation(float tickDelta) {
		return Rotation.rotationFrom(rotationGoal);
	}

	// Builder
	public static Vec3dGoal.BUILDER builder() {
		return new Vec3dGoal.BUILDER();
	}

	public static class BUILDER {
		private final Vec3dGoal goal;

		public BUILDER() {
			goal = new Vec3dGoal();
		}

		public BUILDER goal(Vec3d rotation) {
			goal.rotationGoal = rotation;
			return this;
		}

		public BUILDER mode(RotationMode mode) {
			goal.rotationMode = mode;
			return this;
		}

		public BUILDER maxRotation(float rotation) {
			goal.maxRotation = rotation;
			return this;
		}

		public BUILDER yawRandomness(float randomness) {
			goal.yawRandomness = randomness;
			return this;
		}

		public BUILDER pitchRandomness(float randomness) {
			goal.pitchRandomness = randomness;
			return this;
		}

		public BUILDER fakeRotation(boolean state) {
			goal.fakeRotation = state;
			return this;
		}

		public Vec3dGoal build() {
			return goal;
		}
	}

}
