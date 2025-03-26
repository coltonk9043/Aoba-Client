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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.world.GameMode;

public class EntityUtils {
	protected static final AobaClient AOBA_CLIENT = Aoba.getInstance();
	protected static final MinecraftClient MC = AobaClient.MC;

	/**
	 * Predicates
	 */
	public static final Predicate<Entity> IS_ATTACKABLE = e -> e != null && !e.isRemoved()
			&& (e instanceof LivingEntity && ((LivingEntity) e).getHealth() > 0 || e instanceof EndCrystalEntity
					|| e instanceof ShulkerBulletEntity)
			&& e != MC.player && !(e instanceof FakePlayerEntity) && !AOBA_CLIENT.friendsList.contains(e.getUuid());
	public static final Predicate<AnimalEntity> IS_VALID_ANIMAL = a -> a != null && !a.isRemoved() && a.getHealth() > 0;

	public static final Predicate<PlayerEntity> IS_PLAYER = p -> p != null && !p.isRemoved() && p.getHealth() > 0
			&& !AOBA_CLIENT.friendsList.contains(p.getUuid()) && p != MC.player && !(p instanceof FakePlayerEntity);

	public static Stream<Entity> getAttackableEntities() {
		return StreamSupport.stream(Aoba.getInstance().entityManager.getEntities().spliterator(), true).filter(IS_ATTACKABLE);
	}

	public static Stream<AnimalEntity> getValidAnimals() {
		return StreamSupport.stream(Aoba.getInstance().entityManager.getEntities().spliterator(), true).filter(AnimalEntity.class::isInstance)
				.map(e -> (AnimalEntity) e).filter(IS_VALID_ANIMAL);
	}

	public static GameMode getGameMode(PlayerEntity player) {
		if (player == null)
			return null;
		PlayerListEntry playerListEntry = MC.getNetworkHandler().getPlayerListEntry(player.getUuid());
		if (playerListEntry == null)
			return null;
		return playerListEntry.getGameMode();
	}
}
