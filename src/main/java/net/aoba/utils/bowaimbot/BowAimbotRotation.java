package net.aoba.utils.bowaimbot;

import org.joml.Quaternionf;

import net.aoba.AobaClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record BowAimbotRotation(float yaw, float pitch) {
	private static final MinecraftClient MC = AobaClient.MC;

	public void applyToClientPlayer() {
		float adjustedYaw = BowAimbotUtils.limitAngleChange(MC.player.getYaw(), yaw, 0);
		MC.player.setYaw(adjustedYaw);
		MC.player.setPitch(pitch);
	}

	public void sendPlayerLookPacket() {
		sendPlayerLookPacket(MC.player.isOnGround());
	}

	public void sendPlayerLookPacket(boolean onGround) {
		MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround, false));
	}

	public double getAngleTo(BowAimbotRotation other) {
		float yaw1 = MathHelper.wrapDegrees(yaw);
		float yaw2 = MathHelper.wrapDegrees(other.yaw);
		float diffYaw = MathHelper.wrapDegrees(yaw1 - yaw2);

		float pitch1 = MathHelper.wrapDegrees(pitch);
		float pitch2 = MathHelper.wrapDegrees(other.pitch);
		float diffPitch = MathHelper.wrapDegrees(pitch1 - pitch2);

		return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
	}

	public BowAimbotRotation withYaw(float yaw) {
		return new BowAimbotRotation(yaw, pitch);
	}

	public BowAimbotRotation withPitch(float pitch) {
		return new BowAimbotRotation(yaw, pitch);
	}

	public Vec3d toLookVec() {
		float radPerDeg = MathHelper.RADIANS_PER_DEGREE;
		float pi = MathHelper.PI;

		float adjustedYaw = -MathHelper.wrapDegrees(yaw) * radPerDeg - pi;
		float cosYaw = MathHelper.cos(adjustedYaw);
		float sinYaw = MathHelper.sin(adjustedYaw);

		float adjustedPitch = -MathHelper.wrapDegrees(pitch) * radPerDeg;
		float nCosPitch = -MathHelper.cos(adjustedPitch);
		float sinPitch = MathHelper.sin(adjustedPitch);

		return new Vec3d(sinYaw * nCosPitch, sinPitch, cosYaw * nCosPitch);
	}

	public Quaternionf toQuaternion() {
		float radPerDeg = MathHelper.RADIANS_PER_DEGREE;
		float yawRad = -MathHelper.wrapDegrees(yaw) * radPerDeg;
		float pitchRad = MathHelper.wrapDegrees(pitch) * radPerDeg;

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

	public static BowAimbotRotation wrapped(float yaw, float pitch) {
		return new BowAimbotRotation(MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch));
	}
}
