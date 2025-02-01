/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.entity;

import static net.aoba.AobaClient.MC;

import java.util.function.BiFunction;

import net.minecraft.block.BlockState;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;

public class DamageUtils {
	public static final RaycastFactory HIT_FACTORY = (context, blockPos) -> {
		BlockState blockState = MC.world.getBlockState(blockPos);
		if (blockState.getBlock().getBlastResistance() < 600)
			return null;

		return blockState.getCollisionShape(MC.world, blockPos).raycast(context.start(), context.end(), blockPos);
	};

	public static float crystalDamage(LivingEntity target, Vec3d crystal) {
		return explosionDamage(target, crystal, 12f, false);
	}

	private static float explosionDamage(LivingEntity target, Vec3d explosionPos, float power, boolean predictMovement,
			RaycastFactory raycastFactory) {
		if (target == null)
			return 0f;
		if (target instanceof PlayerEntity player && EntityUtils.getGameMode(player) == GameMode.CREATIVE
				&& !(player instanceof FakePlayerEntity))
			return 0f;

		Vec3d position = predictMovement ? target.getPos().add(target.getVelocity()) : target.getPos();

		Box box = target.getBoundingBox();
		if (predictMovement)
			box = box.offset(target.getVelocity());

		return explosionDamage(target, position, box, explosionPos, power, raycastFactory);
	}

	private static float explosionDamage(LivingEntity target, Vec3d explosionPos, float power,
			boolean predictMovement) {
		return explosionDamage(target, explosionPos, power, predictMovement, HIT_FACTORY);
	}

	public static float explosionDamage(LivingEntity target, Vec3d targetPos, Box targetBox, Vec3d explosionPos,
			float power, RaycastFactory raycastFactory) {
		double modDistance = PlayerUtils.distance(targetPos.x, targetPos.y, targetPos.z, explosionPos.x, explosionPos.y,
				explosionPos.z);
		if (modDistance > power)
			return 0f;

		double exposure = getExposure(explosionPos, targetBox, raycastFactory);
		double impact = (1 - (modDistance / power)) * exposure;
		float damage = (int) ((impact * impact + impact) / 2 * 7 * 12 + 1);

		return calculateReductions(damage, target, MC.world.getDamageSources().explosion(null));
	}

	public static float calculateReductions(float damage, LivingEntity entity, DamageSource damageSource) {
		if (damageSource.isScaledWithDifficulty()) {
			switch (MC.world.getDifficulty()) {
			case EASY -> damage = Math.min(damage / 2 + 1, damage);
			case HARD -> damage *= 1.5f;
			}
		}

		// Armor reduction
		damage = DamageUtil.getDamageLeft(entity, damage, damageSource, getArmor(entity),
				(float) entity.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS));

		// Resistance reduction
		damage = resistanceReduction(entity, damage);

		// Protection reduction
		damage = protectionReduction(entity, damage, damageSource);

		return Math.max(damage, 0);
	}

	private static float protectionReduction(LivingEntity player, float damage, DamageSource source) {
		return DamageUtil.getInflictedDamage(damage, /* protLevel */ 0);
	}

	private static float resistanceReduction(LivingEntity player, float damage) {
		StatusEffectInstance resistance = player.getStatusEffect(StatusEffects.RESISTANCE);
		if (resistance != null) {
			int lvl = resistance.getAmplifier() + 1;
			damage *= (1 - (lvl * 0.2f));
		}

		return Math.max(damage, 0);
	}

	private static float getExposure(Vec3d source, Box box, RaycastFactory raycastFactory) {
		double xDiff = box.maxX - box.minX;
		double yDiff = box.maxY - box.minY;
		double zDiff = box.maxZ - box.minZ;

		double xStep = 1 / (xDiff * 2 + 1);
		double yStep = 1 / (yDiff * 2 + 1);
		double zStep = 1 / (zDiff * 2 + 1);

		if (xStep > 0 && yStep > 0 && zStep > 0) {
			int misses = 0;
			int hits = 0;

			double xOffset = (1 - Math.floor(1 / xStep) * xStep) * 0.5;
			double zOffset = (1 - Math.floor(1 / zStep) * zStep) * 0.5;

			xStep = xStep * xDiff;
			yStep = yStep * yDiff;
			zStep = zStep * zDiff;

			double startX = box.minX + xOffset;
			double startY = box.minY;
			double startZ = box.minZ + zOffset;
			double endX = box.maxX + xOffset;
			double endY = box.maxY;
			double endZ = box.maxZ + zOffset;

			for (double x = startX; x <= endX; x += xStep) {
				for (double y = startY; y <= endY; y += yStep) {
					for (double z = startZ; z <= endZ; z += zStep) {
						Vec3d position = new Vec3d(x, y, z);
						BlockHitResult raycastResult = raycast(new ExposureRaycastContext(position, source),
								raycastFactory);
						if (raycastResult == null || raycastResult.getType() == Type.MISS)
							misses++;
						hits++;
					}
				}
			}
			return (float) misses / hits;
		}

		return 0f;
	}

	private static float getArmor(LivingEntity entity) {
		return (float) Math.floor(entity.getAttributeValue(EntityAttributes.ARMOR));
	}

	private static BlockHitResult raycast(ExposureRaycastContext context, RaycastFactory raycastFactory) {
		return BlockView.raycast(context.start, context.end, context, raycastFactory, ctx -> null);
	}

	public record ExposureRaycastContext(Vec3d start, Vec3d end) {
	}

	@FunctionalInterface
	public interface RaycastFactory extends BiFunction<ExposureRaycastContext, BlockPos, BlockHitResult> {
	}
}
