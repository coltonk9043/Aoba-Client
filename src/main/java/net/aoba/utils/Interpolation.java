package net.aoba.utils;

import static net.aoba.AobaClient.MC;

import java.awt.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Interpolation {
	public static Vec3 interpolatedEyePos() {
		return MC.player.getEyePosition(MC.getDeltaTracker().getGameTimeDeltaPartialTick(false));
	}

	public static Vec3 interpolatedEyeVec() {
		return MC.player.getLightProbePosition(MC.getDeltaTracker().getGameTimeDeltaPartialTick(false));
	}

	public static Vec3 interpolateEntity(Entity entity) {
		double x = interpolateLastTickPos(entity.getX(), entity.xo);
		double y = interpolateLastTickPos(entity.getY(), entity.yo);
		double z = interpolateLastTickPos(entity.getZ(), entity.zo);
		return new Vec3(x, y, z);
	}

	public static double interpolateLastTickPos(double pos, double lastPos) {
		return lastPos + (pos - lastPos) * MC.getDeltaTracker().getGameTimeDeltaPartialTick(false);
	}

	public static Vec3 interpolatedEyeVec(Player player) {
		return player.getLightProbePosition(MC.getDeltaTracker().getGameTimeDeltaPartialTick(false));
	}

	public static Vec3 interpolateVectors(Vec3 vec) {
		double x = vec.x - getRenderPosX();
		double y = vec.y - getRenderPosY();
		double z = vec.z - getRenderPosZ();
		return new Vec3(x, y, z);
	}

	/**
	 * Gets the interpolated {@link Vec3} position of an entity (i.e. position
	 * based on render ticks)
	 *
	 * @param entity    The entity to get the position for
	 * @param tickDelta The render time
	 * @return The interpolated vector of an entity
	 */
	public static Vec3 getRenderPosition(Entity entity, float tickDelta) {
		return new Vec3(entity.getX() - Mth.lerp(tickDelta, entity.xOld, entity.getX()),
				entity.getY() - Mth.lerp(tickDelta, entity.yOld, entity.getY()),
				entity.getZ() - Mth.lerp(tickDelta, entity.zOld, entity.getZ()));
	}

	public static AABB interpolatePos(BlockPos pos) {
		return interpolatePos(pos, 1.0f);
	}

	public static AABB interpolatePos(BlockPos pos, float height) {
		return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + height, pos.getZ() + 1);
	}

	public static AABB getLerpedBox(Entity e, float partialTicks) {
		if (e.isRemoved())
			return e.getBoundingBox();

		Vec3 offset = getRenderPosition(e, partialTicks).subtract(e.position());
		return e.getBoundingBox().move(offset);
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
		return MC.gameRenderer.getMainCamera().position().x;
	}

	public static double getRenderPosY() {
		return MC.gameRenderer.getMainCamera().position().y;
	}

	public static double getRenderPosZ() {
		return MC.gameRenderer.getMainCamera().position().z;
	}

}
