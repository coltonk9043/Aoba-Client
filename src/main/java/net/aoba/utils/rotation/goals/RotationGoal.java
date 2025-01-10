package net.aoba.utils.rotation.goals;

import net.aoba.utils.rotation.Rotation;
import net.aoba.utils.rotation.RotationMode;

public class RotationGoal extends Goal<Rotation> {
	@Override
	public Rotation getGoalRotation(float tickDelta) {
		return this.rotationGoal;
	}

	// Builder
	public static RotationGoal.BUILDER builder() {
		return new RotationGoal.BUILDER();
	}

	public static class BUILDER {
		private RotationGoal goal;

		public BUILDER() {
			goal = new RotationGoal();
		}

		public BUILDER goal(Rotation rotation) {
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

		public RotationGoal build() {
			return goal;
		}
	}
}
