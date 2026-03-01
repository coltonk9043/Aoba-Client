/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public record Rotation(double yaw, double pitch) {
	public static Rotation ZERO = new Rotation(0, 0);
	private static final Minecraft MC = Minecraft.getInstance();

	public Rotation getRadians() {
		return new Rotation(Math.toRadians(yaw), Math.toRadians(pitch));
	}

	public Rotation roundToGCD() {
		double gcd = RotationManager.getGCD();

		// Round to nearest GCD
		double g1 = Math.round(yaw / gcd) * gcd;
		double g2 = Math.round(pitch / gcd) * gcd;

		return new Rotation(g1, Mth.clamp(g2, -90f, 90f));
	}

	public Rotation clamp() {
		return new Rotation(Mth.wrapDegrees(yaw), Mth.wrapDegrees(pitch));
	}

	public double magnitude() {
		float yaw = Mth.wrapDegrees((float) yaw());
		float pitch = Mth.wrapDegrees((float) pitch());
		return Math.sqrt(yaw * yaw + pitch * pitch);
	}

	public Quaternionf toQuaternion() {
		float radPerDeg = Mth.DEG_TO_RAD;
		float yawRad = -Mth.wrapDegrees((float) yaw) * radPerDeg;
		float pitchRad = Mth.wrapDegrees((float) pitch) * radPerDeg;

		float sinYaw = Mth.sin(yawRad / 2);
		float cosYaw = Mth.cos(yawRad / 2);
		float sinPitch = Mth.sin(pitchRad / 2);
		float cosPitch = Mth.cos(pitchRad / 2);

		float x = sinPitch * cosYaw;
		float y = cosPitch * sinYaw;
		float z = -sinPitch * sinYaw;
		float w = cosPitch * cosYaw;

		return new Quaternionf(x, y, z, w);
	}

	public static Rotation difference(Rotation rotation1, Rotation rotation2) {
		return new Rotation(Mth.wrapDegrees(rotation1.yaw - rotation2.yaw),
				Mth.wrapDegrees(rotation1.pitch - rotation2.pitch));
	}

	public static Rotation rotationFrom(Entity target) {
		Minecraft MC = Minecraft.getInstance();
		Vec3 playerPos = MC.player.getEyePosition();
		Vec3 targetPos = target.position().add(0, target.getEyeHeight() / 2.0f, 0);

		double deltaX = targetPos.x - playerPos.x;
		double deltaY = targetPos.y - playerPos.y;
		double deltaZ = targetPos.z - playerPos.z;

		return new Rotation(Mth.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90f), Mth
				.wrapDegrees((-Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ))))));
	}

	public static Rotation rotationFrom(Entity target, float frameDelta) {
		Minecraft MC = Minecraft.getInstance();
		Vec3 playerPos = MC.player.getPosition(frameDelta).add(0, target.getEyeHeight(), 0);
		Vec3 targetPos = target.getPosition(frameDelta).add(0, target.getEyeHeight() / 2.0f, 0);

		double deltaX = targetPos.x - playerPos.x;
		double deltaY = targetPos.y - playerPos.y;
		double deltaZ = targetPos.z - playerPos.z;

		return new Rotation(Mth.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90f), Mth
				.wrapDegrees((-Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ))))));
	}

	public static Rotation rotationFrom(Vec3 vec) {
		Minecraft MC = Minecraft.getInstance();
		Vec3 playerPos = MC.player.getEyePosition();
		double deltaX = vec.x - playerPos.x;
		double deltaY = vec.y - playerPos.y;
		double deltaZ = vec.z - playerPos.z;

		return new Rotation(Mth.wrapDegrees(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90f), Mth
				.wrapDegrees((-Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ))))));
	}

	public static Rotation getPlayerRotationDeltaFromEntity(Entity target) {
		Rotation fromPlayer = rotationFrom(target);
		LocalPlayer player = MC.player;
		Rotation difference = difference(new Rotation(player.getYRot(), player.getXRot()), fromPlayer);
		return difference;
	}

	public static Rotation getPlayerRotationDeltaFromPosition(Vec3 position) {
		Rotation fromPlayer = rotationFrom(position);
		LocalPlayer player = MC.player;
		Rotation difference = difference(new Rotation(player.getYRot(), player.getXRot()), fromPlayer);
		return difference;
	}
}
