package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Mixin(Entity.class)
public interface IEntity {
    @Accessor("movementMultiplier")
    Vec3d getMovementMultiplier();

    @Accessor("movementMultiplier")
    void setMovementMultiplier(Vec3d val);
}