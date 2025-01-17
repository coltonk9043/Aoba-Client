/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
	 * @param fs DataInputStream to read from.
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
