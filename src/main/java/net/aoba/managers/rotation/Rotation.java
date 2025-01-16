package net.aoba.managers.rotation;

import org.joml.Quaternionf;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record Rotation(double yaw, double pitch) {
	public static Rotation ZERO = new Rotation(0, 0);
	private static MinecraftClient MC = MinecraftClient.getInstance();

	public Rotation getRadians() {
		return new Rotation(Math.toRadians(yaw), Math.toRadians(pitch));
	}

	public Rotation roundToGCD() {
		double gcd = RotationManager.getGCD();
		ClientPlayerEntity player = MC.player;
		// Rotation serverRotation = RotationManager.serverRotation;

		// Get Deltas
		double deltaYaw = this.yaw - player.getYaw();
		double deltaPitch = this.pitch - player.getPitch();

		// Round to nearest GCD
		double g1 = Math.round(deltaYaw / gcd) * gcd;
		double g2 = Math.round(deltaPitch / gcd) * gcd;

		// Add corrected rotation to server rotation
		double yaw = player.getYaw() + g1;
		double pitch = player.getPitch() + g2;

		return new Rotation(yaw, MathHelper.clamp(pitch, -90f, 90f));
	}

	public double magnitude() {
		float yaw = MathHelper.wrapDegrees((float) yaw());
		float pitch = MathHelper.wrapDegrees((float) pitch());
		return Math.sqrt(yaw * yaw + pitch * pitch);
	}

	public Quaternionf toQuaternion() {
		float radPerDeg = MathHelper.RADIANS_PER_DEGREE;
		float yawRad = -MathHelper.wrapDegrees((float) yaw) * radPerDeg;
		float pitchRad = MathHelper.wrapDegrees((float) pitch) * radPerDeg;

		float sinYaw = MathHelper.sin(yawRad / 2);
		float cosYaw = MathHelper.cos(yawRad / 2);
		float sinPitch = MathHelper.sin(pitchRad / 2);
		float cosPitch = MathHelper.cos(pitchRad / 2);

		float x = sinPitch * cosYaw;
		float y = cosPitch * sinYaw;
		float z = -sinPitch * sinYaw;
		float w = cosPitch * cosYaw;

		return new Quaternionf(x, y, z, w);
	}

	public static Rotation difference(Rotation rotation1, Rotation rotation2) {
		return new Rotation(MathHelper.wrapDegrees(rotation1.yaw - rotation2.yaw),
				MathHelper.wrapDegrees(rotation1.pitch - rotation2.pitch));
	}

	public static Rotation rotationFrom(Entity target) {
		MinecraftClient MC = MinecraftClient.getInstance();
		Vec3d playerPos = MC.player.getEyePos();
		Vec3d targetPos = target.getPos().add(0, target.getStandingEyeHeight() / 2.0f, 0);

		double deltaX = targetPos.x - playerPos.x;
		double deltaY = targetPos.y - playerPos.y;
		double deltaZ = targetPos.z - playerPos.z;

		return new Rotation(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90f), MathHelper
				.wrapDegrees((-Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ))))));
	}

	public static Rotation rotationFrom(Entity target, float frameDelta) {
		MinecraftClient MC = MinecraftClient.getInstance();
		Vec3d playerPos = MC.player.getLerpedPos(frameDelta).add(0, target.getStandingEyeHeight(), 0);
		Vec3d targetPos = target.getLerpedPos(frameDelta).add(0, target.getStandingEyeHeight() / 2.0f, 0);

		double deltaX = targetPos.x - playerPos.x;
		double deltaY = targetPos.y - playerPos.y;
		double deltaZ = targetPos.z - playerPos.z;

		return new Rotation(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90f), MathHelper
				.wrapDegrees((-Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ))))));
	}

	public static Rotation rotationFrom(Vec3d vec) {
		MinecraftClient MC = MinecraftClient.getInstance();
		Vec3d playerPos = MC.player.getEyePos();
		double deltaX = vec.x - playerPos.x;
		double deltaY = vec.y - playerPos.y;
		double deltaZ = vec.z - playerPos.z;

		return new Rotation(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90f), MathHelper
				.wrapDegrees((-Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ))))));
	}

	public static Rotation getPlayerRotationDeltaFromEntity(Entity target) {
		Rotation fromPlayer = rotationFrom(target);
		ClientPlayerEntity player = MC.player;
		Rotation difference = difference(new Rotation(player.getYaw(), player.getPitch()), fromPlayer);
		return difference;
	}

	public static Rotation getPlayerRotationDeltaFromPosition(Vec3d position) {
		Rotation fromPlayer = rotationFrom(position);
		ClientPlayerEntity player = MC.player;
		Rotation difference = difference(new Rotation(player.getYaw(), player.getPitch()), fromPlayer);
		return difference;
	}
}
