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
import net.aoba.utils.entity.BodyPart;
import net.aoba.utils.entity.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityGoal extends Goal<Entity> {

	private BodyPart bodyPart = BodyPart.CHEST;

	@Override
	public Rotation getGoalRotation(float tickDelta) {
		Vec3 targetPos = EntityUtils.getBodyPartPosition(rotationGoal, bodyPart, tickDelta);
		return Rotation.rotationFrom(targetPos);
	}

	// Builder
	public static EntityGoal.BUILDER builder() {
		return new EntityGoal.BUILDER();
	}

	public static class BUILDER {
		private final EntityGoal goal;

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

		public BUILDER fakeRotation(boolean state) {
			goal.fakeRotation = state;
			return this;
		}

		public BUILDER moveFix(boolean state) {
			goal.moveFix = state;
			return this;
		}

		public BUILDER bodyPart(BodyPart part) {
			goal.bodyPart = part;
			return this;
		}

		public EntityGoal build() {
			return goal;
		}
	}

}
