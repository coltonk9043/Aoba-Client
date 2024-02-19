package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface IEntityVelocityUpdateS2CPacket {
	@Mutable
    @Accessor("velocityX")
    void setVelocityX(int velocityX);

    @Mutable
    @Accessor("velocityY")
    void setVelocityY(int velocityY);

    @Mutable
    @Accessor("velocityZ")
    void setVelocityZ(int velocityZ);

    @Accessor("id")
    int getId();
    
    @Accessor("velocityX")
    int getVelocityX();

    @Accessor("velocityY")
    int getVelocityY();

    @Accessor("velocityZ")
    int getVelocity();
}