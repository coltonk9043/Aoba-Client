package net.aoba.utils.rotation.goals;

import net.aoba.utils.rotation.Rotation;
import net.aoba.utils.rotation.RotationMode;
import net.minecraft.entity.Entity;

public class EntityGoal extends Goal<Entity> {

	@Override
	public Rotation getGoalRotation(float tickDelta) {
		return Rotation.rotationFrom(rotationGoal, tickDelta);
	}

	// Builder
	public static EntityGoal.BUILDER builder() {
		return new EntityGoal.BUILDER();
	}

	public static class BUILDER {
		private EntityGoal goal;

		public BUILDER() {
			goal = new EntityGoal();
		}

		public BUILDER goal(Entity rotation) {
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

		public EntityGoal build() {
			return goal;
		}
	}

}
