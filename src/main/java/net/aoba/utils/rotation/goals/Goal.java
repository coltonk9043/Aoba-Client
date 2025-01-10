package net.aoba.utils.rotation.goals;

import net.aoba.utils.rotation.Rotation;
import net.aoba.utils.rotation.RotationMode;

/**
 * A class that represents a desire to rotate the players camera to a certain
 * rotation.
 */
public abstract class Goal<T> {

	protected RotationMode rotationMode = RotationMode.SMOOTH;
	protected T rotationGoal;
	protected float maxRotation = 10.0f;
	protected float yawRandomness = 0f;
	protected float pitchRandomness = 0f;

	/**
	 * Getter for rotationGoal
	 * 
	 * @return Rotation goal.
	 */
	public T getGoal() {
		return rotationGoal;
	}

	/**
	 * Getter for rotationMode
	 * 
	 * @return Rotation Mode of the goal.
	 */
	public RotationMode getRotationMode() {
		return rotationMode;
	}

	/**
	 * Getter for rotationGoal
	 * 
	 * @return Rotation goal.
	 */
	public abstract Rotation getGoalRotation(float tickDelta);

	/**
	 * Getter for maxRotation
	 * 
	 * @return Max Rotation of the goal.
	 */
	public float getMaxRotation() {
		return maxRotation;
	}

	/**
	 * Getter for yawRandomness
	 * 
	 * @return Yaw Randomness of the goal.
	 */
	public float getYawRandomness() {
		return yawRandomness;
	}

	/**
	 * Getter for pitchRandomness
	 * 
	 * @return Pitch Randomness of the goal.
	 */
	public float getPitchRandomness() {
		return pitchRandomness;
	}

}
