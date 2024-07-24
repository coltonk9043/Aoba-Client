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

    public static Stream<Entity> getAttackableEntities() {
        return StreamSupport.stream(MC.world.getEntities().spliterator(), true)
                .filter(IS_ATTACKABLE);
    }

    public static final Predicate<Entity> IS_ATTACKABLE = e -> e != null
            && !e.isRemoved()
            && (e instanceof LivingEntity && ((LivingEntity) e).getHealth() > 0
            || e instanceof EndCrystalEntity
            || e instanceof ShulkerBulletEntity)
            && e != MC.player && !(e instanceof FakePlayerEntity)
            && !AOBA_CLIENT.friendsList.contains(e.getUuid());

    public static Stream<AnimalEntity> getValidAnimals() {
        return StreamSupport.stream(MC.world.getEntities().spliterator(), true)
                .filter(AnimalEntity.class::isInstance).map(e -> (AnimalEntity) e)
                .filter(IS_VALID_ANIMAL);
    }

    public static final Predicate<AnimalEntity> IS_VALID_ANIMAL =
            a -> a != null && !a.isRemoved() && a.getHealth() > 0;

    public static final Predicate<PlayerEntity> IS_PLAYER =
            p -> p != null && !p.isRemoved() && p.getHealth() > 0 && !AOBA_CLIENT.friendsList.contains(p.getUuid()) && p != MC.player && !(p instanceof FakePlayerEntity);

    public static Vec3d getLerpedPos(Entity e, float partialTicks) {
        if (e.isRemoved())
            return e.getPos();

        double x = MathHelper.lerp(partialTicks, e.lastRenderX, e.getX());
        double y = MathHelper.lerp(partialTicks, e.lastRenderY, e.getY());
        double z = MathHelper.lerp(partialTicks, e.lastRenderZ, e.getZ());
        return new Vec3d(x, y, z);
    }

    public static Box getLerpedBox(Entity e, float partialTicks) {
        if (e.isRemoved())
            return e.getBoundingBox();

        Vec3d offset = getLerpedPos(e, partialTicks).subtract(e.getPos());
        return e.getBoundingBox().offset(offset);
    }
}
