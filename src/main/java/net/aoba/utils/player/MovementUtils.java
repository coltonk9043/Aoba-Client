package net.aoba.utils.player;

import net.minecraft.client.Minecraft;

public class MovementUtils {
	private static final Minecraft MC = Minecraft.getInstance();
	
	public static void stopMoving() {
		MC.options.keyUp.setDown(false);
		MC.options.keyDown.setDown(false);
		MC.options.keyLeft.setDown(false);
		MC.options.keyRight.setDown(false);
		MC.options.keyShift.setDown(false);
		MC.options.keyJump.setDown(false);
	}
}
