package net.aoba.mixin;

import net.aoba.interfaces.IHorseBaseEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorseEntity.class)
public abstract class HorseBaseEntityMixin implements IHorseBaseEntity {
    @Shadow
    protected abstract void setHorseFlag(int bitmask, boolean flag);

    @Override
    public void setSaddled(boolean saddled) {
        setHorseFlag(4, saddled);
    }
}