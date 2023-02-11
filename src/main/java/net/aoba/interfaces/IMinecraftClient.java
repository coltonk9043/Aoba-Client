package net.aoba.interfaces;

import net.minecraft.client.util.Session;

public interface IMinecraftClient {
	public void rightClick();
	
	public void setItemUseCooldown(int itemUseCooldown);

	public int getItemUseCooldown();
	
	public IWorld getWorld();
	
	public void setSession(Session session);
}
