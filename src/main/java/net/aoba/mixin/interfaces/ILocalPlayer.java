package net.aoba.mixin.interfaces;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LocalPlayer.class)
public interface ILocalPlayer {
	@Accessor("positionReminder")
	int getTicksSinceLastPositionPacketSent();

	@Accessor("lastOnGround")
	boolean getLastOnGround();

	@Accessor("lastHorizontalCollision")
	boolean getLastHorizontalCollision();

	@Accessor("autoJumpEnabled")
	boolean getAutoJumpEnabled();

	@Accessor("xLast")
	double getXLast();

	@Accessor("yLast")
	double getYLast();

	@Accessor("zLast")
	double getZLast();

	@Accessor("yRotLast")
	float getYRotLast();

	@Accessor("xRotLast")
	float getXRotLast();

	@Accessor("positionReminder")
	void setTicksSinceLastPositionPacketSent(int value);

	@Accessor("lastOnGround")
	void setLastOnGround(boolean value);

	@Accessor("lastHorizontalCollision")
	void setLastHorizontalCollision(boolean value);

	@Accessor("autoJumpEnabled")
	void setAutoJumpEnabled(boolean value);

	@Accessor("xLast")
	void setXLast(double value);

	@Accessor("yLast")
	void setYLast(double value);

	@Accessor("zLast")
	void setZLast(double value);

	@Accessor("yRotLast")
	void setYRotLast(float value);

	@Accessor("xRotLast")
	void setXRotLast(float value);
}
