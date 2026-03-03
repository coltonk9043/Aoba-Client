package net.aoba.mixin.interfaces;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface IEntity {
    @Accessor("stuckSpeedMultiplier")
    Vec3 getMovementMultiplier();

    @Accessor("stuckSpeedMultiplier")
    void setMovementMultiplier(Vec3 val);
}