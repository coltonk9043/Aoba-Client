package aoba.main.cmd;

import net.minecraft.client.Minecraft;

public abstract class Command {
	protected String command;
	protected String description;
	protected Minecraft mc = Minecraft.getInstance();
	
	public abstract void command(String[] parameters);
	
	public String getCommand() {
		return this.command;
	}
	
	public String getDescription() {
		return this.description;
	}
	
}
