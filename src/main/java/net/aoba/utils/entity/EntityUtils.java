/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.entity;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public class EntityUtils {
	protected static final AobaClient AOBA_CLIENT = Aoba.getInstance();
	protected static final Minecraft MC = AobaClient.MC;

	/**
	 * Predicates
	 */
	public static final Predicate<Entity> IS_ATTACKABLE = e -> e != null && !e.isRemoved()
			&& (e instanceof LivingEntity && ((LivingEntity) e).getHealth() > 0 || e instanceof EndCrystal
					|| e instanceof ShulkerBullet)
			&& e != MC.player && !(e instanceof FakePlayerEntity) && !AOBA_CLIENT.friendsList.contains(e.getUUID());
	public static final Predicate<Animal> IS_VALID_ANIMAL = a -> a != null && !a.isRemoved() && a.getHealth() > 0;

	public static final Predicate<Player> IS_PLAYER = p -> p != null && !p.isRemoved() && p.getHealth() > 0
			&& !AOBA_CLIENT.friendsList.contains(p.getUUID()) && p != MC.player && !(p instanceof FakePlayerEntity);

	public static Stream<Entity> getAttackableEntities() {
		return StreamSupport.stream(Aoba.getInstance().entityManager.getEntities().spliterator(), true).filter(IS_ATTACKABLE);
	}

	public static Stream<Animal> getValidAnimals() {
		return StreamSupport.stream(Aoba.getInstance().entityManager.getEntities().spliterator(), true).filter(Animal.class::isInstance)
				.map(e -> (Animal) e).filter(IS_VALID_ANIMAL);
	}

	public static boolean isInFOV(Entity entity, float fov) {
		return isInFOV(entity.getEyePosition(), fov);
	}

	public static boolean isInFOV(Entity entity, BodyPart part, float fov) {
		return isInFOV(getBodyPartPosition(entity, part, 1.0f), fov);
	}

	public static boolean isInFOV(Vec3 position, float fov) {
		if (fov >= 360.0f)
			return true;
		Vec3 viewVector = MC.player.getViewVector(1.0f);
		Vec3 vecToTarget = position.subtract(MC.player.getEyePosition()).normalize();
		double cosAngle = viewVector.dot(vecToTarget);
		return cosAngle >= Math.cos(Math.toRadians(fov / 2.0f));
	}

	public static Vec3 getBodyPartPosition(Entity entity, BodyPart part, float frameDelta) {
		Vec3 entityPos = entity.getPosition(frameDelta);
		double entityEyeHeight = entity.getEyeHeight();
		double bbWidth = entity.getBbWidth();

		double yaw;
		if(entity instanceof LivingEntity living)
			yaw = Math.toRadians(living.yBodyRot);
		else
			yaw = Math.toRadians(entity.getYRot());
			
		double rightX = -Math.cos(yaw);
		double rightZ = -Math.sin(yaw);

		switch (part) {
			case HEAD:
				return entityPos.add(0, entityEyeHeight * 0.95, 0);
			case CHEST:
				return entityPos.add(0, entityEyeHeight * 0.65, 0);
			case LEFT_ARM:
				return entityPos.add(-rightX * bbWidth * 0.4, entityEyeHeight * 0.6, -rightZ * bbWidth * 0.4);
			case RIGHT_ARM:
				return entityPos.add(rightX * bbWidth * 0.4, entityEyeHeight * 0.6, rightZ * bbWidth * 0.4);
			case LEFT_LEG:
				return entityPos.add(-rightX * bbWidth * 0.2, entityEyeHeight * 0.3, -rightZ * bbWidth * 0.2);
			case RIGHT_LEG:
				return entityPos.add(rightX * bbWidth * 0.2, entityEyeHeight * 0.3, rightZ * bbWidth * 0.2);
			default:
				return entityPos.add(0, entityEyeHeight * 0.65, 0);
		}
	}

	public static GameType getGameMode(Player player) {
		if (player == null)
			return null;
		PlayerInfo playerListEntry = MC.getConnection().getPlayerInfo(player.getUUID());
		if (playerListEntry == null)
			return null;
		return playerListEntry.getGameMode();
	}
}
