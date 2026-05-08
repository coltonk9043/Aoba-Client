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
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

public class DamageUtils {
	public static final RaycastFactory HIT_FACTORY = (context, blockPos) -> {
		BlockState blockState = MC.level.getBlockState(blockPos);
		if (blockState.getBlock().getExplosionResistance() < 600)
			return null;

		return blockState.getCollisionShape(MC.level, blockPos).clip(context.start(), context.end(), blockPos);
	};

	public static final RaycastFactory ANCHOR_HIT_FACTORY = (context, blockPos) -> {
		BlockState blockState = MC.level.getBlockState(blockPos);
		if (blockState.getBlock().getExplosionResistance() < 3)
			return null;

		return blockState.getCollisionShape(MC.level, blockPos).clip(context.start(), context.end(), blockPos);
	};

	public static float crystalDamage(LivingEntity target, Vec3 crystal) {
		return explosionDamage(target, crystal, 12f, false, MC.level.damageSources().explosion(null));
	}

	public static float anchorDamage(LivingEntity target, Vec3 anchorPos) {
		return explosionDamage(target, anchorPos, 10f, false,
				MC.level.damageSources().badRespawnPointExplosion(anchorPos), ANCHOR_HIT_FACTORY);
	}

	private static float explosionDamage(LivingEntity target, Vec3 explosionPos, float power, boolean predictMovement,
			DamageSource damageSource, RaycastFactory raycastFactory) {
		if (target == null)
			return 0f;
		if (target instanceof Player player && EntityUtils.getGameMode(player) == GameType.CREATIVE
				&& !(player instanceof FakePlayerEntity))
			return 0f;

		Vec3 position = predictMovement ? target.position().add(target.getDeltaMovement()) : target.position();

		AABB box = target.getBoundingBox();
		if (predictMovement)
			box = box.move(target.getDeltaMovement());

		return explosionDamage(target, position, box, explosionPos, power, damageSource, raycastFactory);
	}

	private static float explosionDamage(LivingEntity target, Vec3 explosionPos, float power, boolean predictMovement,
			DamageSource damageSource) {
		return explosionDamage(target, explosionPos, power, predictMovement, damageSource, HIT_FACTORY);
	}

	public static float explosionDamage(LivingEntity target, Vec3 targetPos, AABB targetBox, Vec3 explosionPos,
			float power, DamageSource damageSource, RaycastFactory raycastFactory) {
		double modDistance = PlayerUtils.distance(targetPos.x, targetPos.y, targetPos.z, explosionPos.x, explosionPos.y,
				explosionPos.z);
		if (modDistance > power)
			return 0f;

		double exposure = getExposure(explosionPos, targetBox, raycastFactory);
		double impact = (1 - (modDistance / power)) * exposure;
		float damage = (int) ((impact * impact + impact) / 2 * 7 * power + 1);

		return calculateReductions(damage, target, damageSource);
	}

	public static float calculateReductions(float damage, LivingEntity entity, DamageSource damageSource) {
		if (damageSource.scalesWithDifficulty()) {
			switch (MC.level.getDifficulty()) {
			case EASY -> damage = Math.min(damage / 2 + 1, damage);
			case HARD -> damage *= 1.5f;
			}
		}

		// Armor reduction
		damage = CombatRules.getDamageAfterAbsorb(entity, damage, damageSource, getArmor(entity),
				(float) entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));

		// Resistance reduction
		damage = resistanceReduction(entity, damage);

		// Protection reduction
		damage = protectionReduction(entity, damage, damageSource);

		return Math.max(damage, 0);
	}

	private static float protectionReduction(LivingEntity player, float damage, DamageSource source) {
		return CombatRules.getDamageAfterMagicAbsorb(damage, /* protLevel */ 0);
	}

	private static float resistanceReduction(LivingEntity player, float damage) {
		MobEffectInstance resistance = player.getEffect(MobEffects.RESISTANCE);
		if (resistance != null) {
			int lvl = resistance.getAmplifier() + 1;
			damage *= (1 - (lvl * 0.2f));
		}

		return Math.max(damage, 0);
	}

	private static float getExposure(Vec3 source, AABB box, RaycastFactory raycastFactory) {
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
						Vec3 position = new Vec3(x, y, z);
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
		return (float) Math.floor(entity.getAttributeValue(Attributes.ARMOR));
	}

	private static BlockHitResult raycast(ExposureRaycastContext context, RaycastFactory raycastFactory) {
		return BlockGetter.traverseBlocks(context.start, context.end, context, raycastFactory, ctx -> null);
	}

	public record ExposureRaycastContext(Vec3 start, Vec3 end) {
	}

	@FunctionalInterface
	public interface RaycastFactory extends BiFunction<ExposureRaycastContext, BlockPos, BlockHitResult> {
	}
}
