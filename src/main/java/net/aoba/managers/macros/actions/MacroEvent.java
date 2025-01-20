/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.macros.actions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.MinecraftClient;

/**
 * Abstract class representing an event in a Macro.
 */
public abstract class MacroEvent {
	protected static MinecraftClient MC = MinecraftClient.getInstance();

	protected long timestamp;

	public MacroEvent() {
	}

	public MacroEvent(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Writes the data in the Macro out to a DataOutputStream
	 * 
	 * @param fs DataOutputStream to write to.
	 */
	public void write(DataOutputStream fs) throws IOException {
		fs.writeLong(timestamp);
	}

	/**
	 * Reads the data from a DataInputStream
	 * 
	 * @param in DataInputStream to read from.
	 */
	public void read(DataInputStream in) throws IOException {
		timestamp = in.readLong();
	}

	/**
	 * Executes the event.
	 */
	public abstract void execute();

	/**
	 * Getter for the Timestamp
	 * 
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}
}
