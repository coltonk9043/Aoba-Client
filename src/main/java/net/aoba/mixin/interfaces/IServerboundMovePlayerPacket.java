package net.aoba.mixin.interfaces;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundMovePlayerPacket.class)
public interface IServerboundMovePlayerPacket {
	@Accessor("x")
	double getX(); 
	
	@Accessor("y")
	double getY(); 
	
	@Accessor("z")
	double getZ(); 
	
	@Accessor("yRot")
	float getYaw(); 
	
	@Accessor("xRot")
	float getPitch(); 
	
    @Mutable
    @Accessor("x")
    void setX(double newX);
    
    @Mutable
    @Accessor("y")
    void setY(double newY);

    @Mutable
    @Accessor("z")
    void setZ(double newZ);
    
    @Mutable
    @Accessor("yRot")
    void setYaw(float newYaw);
    
    @Mutable
    @Accessor("xRot")
    void setPitch(float newPitch);
    
    @Mutable
    @Accessor("onGround")
    void setOnGround(boolean onGround);
}