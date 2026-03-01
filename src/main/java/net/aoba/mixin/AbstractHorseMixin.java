package net.aoba.mixin;

import net.aoba.interfaces.IAbstractHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin implements IAbstractHorse {
    @Shadow
    protected abstract void setFlag(int bitmask, boolean flag);

    @Override
    public void setSaddled(boolean saddled) {
        setFlag(4, saddled);
    }
}