package net.aoba.utils;

import static net.aoba.AobaClient.MC;

import java.awt.Color;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Interpolation {
	public static Vec3d interpolatedEyePos() {
		return MC.player.getCameraPosVec(MC.getRenderTickCounter().getTickProgress(false));
	}

	public static Vec3d interpolatedEyeVec() {
		return MC.player.getClientCameraPosVec(MC.getRenderTickCounter().getTickProgress(false));
	}

	public static Vec3d interpolateEntity(Entity entity) {
		double x = interpolateLastTickPos(entity.getX(), entity.lastX);
		double y = interpolateLastTickPos(entity.getY(), entity.lastY);
		double z = interpolateLastTickPos(entity.getZ(), entity.lastZ);
		return new Vec3d(x, y, z);
	}

	public static double interpolateLastTickPos(double pos, double lastPos) {
		return lastPos + (pos - lastPos) * MC.getRenderTickCounter().getTickProgress(false);
	}

	public static Vec3d interpolatedEyeVec(PlayerEntity player) {
		return player.getClientCameraPosVec(MC.getRenderTickCounter().getTickProgress(false));
	}

	public static Vec3d interpolateVectors(Vec3d vec) {
		double x = vec.x - getRenderPosX();
		double y = vec.y - getRenderPosY();
		double z = vec.z - getRenderPosZ();
		return new Vec3d(x, y, z);
	}

	/**
	 * Gets the interpolated {@link Vec3d} position of an entity (i.e. position
	 * based on render ticks)
	 *
	 * @param entity    The entity to get the position for
	 * @param tickDelta The render time
	 * @return The interpolated vector of an entity
	 */
	public static Vec3d getRenderPosition(Entity entity, float tickDelta) {
		return new Vec3d(entity.getX() - MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()),
				entity.getY() - MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()),
				entity.getZ() - MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()));
	}

	public static Box interpolatePos(BlockPos pos) {
		return interpolatePos(pos, 1.0f);
	}

	public static Box interpolatePos(BlockPos pos, float height) {
		return new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + height, pos.getZ() + 1);
	}

	public static Box getLerpedBox(Entity e, float partialTicks) {
		if (e.isRemoved())
			return e.getBoundingBox();

		Vec3d offset = getRenderPosition(e, partialTicks).subtract(e.getPos());
		return e.getBoundingBox().offset(offset);
	}

	public static Color interpolateColorC(Color color1, Color color2, float amount) {
		amount = Math.min(1, Math.max(0, amount));
		return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
				interpolateInt(color1.getGreen(), color2.getGreen(), amount),
				interpolateInt(color1.getBlue(), color2.getBlue(), amount),
				interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
	}

	public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
		return (int) interpolate(oldValue, newValue, (float) interpolationValue);
	}

	public static float interpolateFloat(float prev, float value, float factor) {
		return prev + ((value - prev) * factor);
	}

	public static double interpolate(double oldValue, double newValue, double interpolationValue) {
		return (oldValue + (newValue - oldValue) * interpolationValue);
	}

	public static double getRenderPosX() {
		return MC.gameRenderer.getCamera().getPos().x;
	}

	public static double getRenderPosY() {
		return MC.gameRenderer.getCamera().getPos().y;
	}

	public static double getRenderPosZ() {
		return MC.gameRenderer.getCamera().getPos().z;
	}

}
