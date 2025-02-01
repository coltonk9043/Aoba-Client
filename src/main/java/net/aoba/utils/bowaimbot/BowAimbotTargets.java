/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.bowaimbot;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.utils.entity.FakePlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BowAimbotTargets {
    protected static final AobaClient AOBA_CLIENT = Aoba.getInstance();
    protected static final MinecraftClient MC = AobaClient.MC;

    public static final Predicate<Entity> IS_ATTACKABLE = e -> e != null
            && !e.isRemoved()
            && (e instanceof LivingEntity && ((LivingEntity) e).getHealth() > 0
            || e instanceof EndCrystalEntity
            || e instanceof ShulkerBulletEntity)
            && e != MC.player && !(e instanceof FakePlayerEntity)
            && !AOBA_CLIENT.friendsList.contains(e.getUuid());
}
