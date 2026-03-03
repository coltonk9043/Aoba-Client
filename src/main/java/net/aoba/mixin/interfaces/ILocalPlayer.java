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

	@Accessor("positionReminder")
	void setTicksSinceLastPositionPacketSent(int value);

	@Accessor("lastOnGround")
	void setLastOnGround(boolean value);

	@Accessor("lastHorizontalCollision")
	void setLastHorizontalCollision(boolean value);

	@Accessor("autoJumpEnabled")
	void setAutoJumpEnabled(boolean value);
}
