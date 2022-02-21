package net.aoba.interfaces;

import net.minecraft.client.util.Session;

public interface IMinecraftClient {
	public void setSession(Session session);
	public int getRightClickDelay();
	public void setRightClickDelay(int delay);
	
}
