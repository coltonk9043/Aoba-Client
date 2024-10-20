package net.aoba.macros.actions;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.minecraft.client.MinecraftClient;

public abstract class MacroEvent {
	protected static MinecraftClient MC = MinecraftClient.getInstance();
	
	protected final long timestamp;
	
	public MacroEvent(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public abstract void write(FileOutputStream fs);
	public abstract void read(FileInputStream fs);
	public abstract void execute();
	
	public long getTimestamp() {
		return timestamp;
	}
}
