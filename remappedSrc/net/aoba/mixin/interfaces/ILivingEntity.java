package net.aoba.mixin.interfaces;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface ILivingEntity {
    @Accessor("jumpingCooldown")
    int getJumpCooldown();

    @Accessor("jumpingCooldown")
    void setJumpCooldown(int val);
}