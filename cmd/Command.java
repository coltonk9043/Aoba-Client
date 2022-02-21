package net.aoba.cmd;

import net.minecraft.client.MinecraftClient;

public abstract class Command {
	protected String command;
	protected String description;
	protected MinecraftClient mc = MinecraftClient.getInstance();
	
	public abstract void command(String[] parameters);
	
	public String getCommand() {
		return this.command;
	}
	
	public String getDescription() {
		return this.description;
	}
	
}
