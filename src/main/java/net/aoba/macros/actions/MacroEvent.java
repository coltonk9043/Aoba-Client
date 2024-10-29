package net.aoba.macros.actions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.client.MinecraftClient;

public abstract class MacroEvent {
	protected static MinecraftClient MC = MinecraftClient.getInstance();
	
	protected long timestamp;
	
	public MacroEvent() { }
	
	public MacroEvent(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public void write(DataOutputStream fs) throws IOException {
		fs.writeUTF(KeyClickMacroEvent.class.getName());
		fs.writeLong(timestamp);
	}
	
	public void read(DataInputStream in) throws IOException {
		timestamp = in.readLong();
	}
	
	public abstract void execute();
	
	public long getTimestamp() {
		return timestamp;
	}
}
