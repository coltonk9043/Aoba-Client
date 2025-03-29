package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public interface IClientPlayerEntity {
	// @Accessor("lastX")
	// double getLastX();

	// @Accessor("lastBaseY")
	// double getLastY();

	// @Accessor("lastZ")
	// double getLastZ();

	// @Accessor("lastYaw")
	// float getLastYaw();

	// @Accessor("lastPitch")
	// float getLastPitch();

	@Accessor("ticksSinceLastPositionPacketSent")
	int getTicksSinceLastPositionPacketSent();

	@Accessor("lastOnGround")
	boolean getLastOnGround();

	@Accessor("lastHorizontalCollision")
	boolean getLastHorizontalCollision();

	@Accessor("autoJumpEnabled")
	boolean getAutoJumpEnabled();

	// @Accessor("lastX")
	// void setLastX(double value);

	// @Accessor("lastBaseY")
	// void setLastY(double value);

	// @Accessor("lastZ")
	// void setLastZ(double value);

	// @Accessor("lastYaw")
	// void setLastYaw(float value);

	// @Accessor("lastPitch")
	// void setLastPitch(float value);

	@Accessor("ticksSinceLastPositionPacketSent")
	void setTicksSinceLastPositionPacketSent(int value);

	@Accessor("lastOnGround")
	void setLastOnGround(boolean value);

	@Accessor("lastHorizontalCollision")
	void setLastHorizontalCollision(boolean value);

	@Accessor("autoJumpEnabled")
	void setAutoJumpEnabled(boolean value);
}
