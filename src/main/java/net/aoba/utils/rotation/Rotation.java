package net.aoba.utils.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record Rotation(double yaw, double pitch) {
	public static Rotation ZERO = new Rotation(0, 0);
	
	public Rotation getRadians() {
		return new Rotation(Math.toRadians(yaw), Math.toRadians(pitch));
	}
	
	public Rotation roundToGCD() {
		double gcd = RotationManager.getGCD();
		Rotation serverRotation = RotationManager.serverRotation;

		// Get Deltas
		double deltaYaw = this.yaw - serverRotation.yaw;
		double deltaPitch = this.pitch - serverRotation.pitch;
		
		// Round to nearest GCD
		double g1 = Math.round(deltaYaw / gcd) * gcd;
		double g2 = Math.round(deltaPitch / gcd) * gcd;

		// Add corrected rotation to server rotation
		double yaw = serverRotation.yaw + g1;
		double pitch = serverRotation.pitch + g2;

		return new Rotation(yaw, MathHelper.clamp(pitch, -90f, 90f));
	}
	
	public static Rotation difference(Rotation rotation1, Rotation rotation2) {
		return new Rotation(MathHelper.wrapDegrees(rotation1.yaw - rotation2.yaw), MathHelper.wrapDegrees(rotation1.pitch - rotation2.pitch));
	}
	
	public static Rotation rotationFrom(Entity target) {
		MinecraftClient MC = MinecraftClient.getInstance();
		Vec3d playerPos = MC.player.getPos();
		Vec3d targetPos = target.getPos();

		double deltaX = targetPos.x - playerPos.x;
		double deltaY = targetPos.y - playerPos.y;
		double deltaZ = targetPos.z - playerPos.z;

		return new Rotation(
			MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90f),
			MathHelper.wrapDegrees((-Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)))))
		);
	}
	
	public static Rotation rotationFrom(Vec3d vec) {
		MinecraftClient MC = MinecraftClient.getInstance();
		Vec3d playerPos = MC.player.getPos();

		double deltaX = vec.x - playerPos.x;
		double deltaY = vec.y - playerPos.y;
		double deltaZ = vec.z - playerPos.z;

		return new Rotation(
			MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90f),
			MathHelper.wrapDegrees((-Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)))))
		);
	}
	
	public static Rotation getPlayerRotationDeltaFromEntity(Entity target) {
		Rotation fromPlayer = rotationFrom(target);
		Rotation difference = difference(new Rotation(RotationManager.serverRotation.yaw(), RotationManager.serverRotation.pitch()), fromPlayer);
		return difference;
	}
	
	public static Rotation getPlayerRotationDeltaFromPosition(Vec3d position) {
		Rotation fromPlayer = rotationFrom(position);
		Rotation difference = difference(new Rotation(RotationManager.serverRotation.yaw(), RotationManager.serverRotation.pitch()), fromPlayer);
		return difference;
	}
}
