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
package net.aoba.managers.macros;

import java.io.File;
import java.util.LinkedList;

import net.aoba.managers.macros.actions.MacroEvent;
import net.minecraft.client.MinecraftClient;

/**
 * Represents a Macro that contains a list of events.
 */
public class Macro {
	private String name;
	private String filePath;
	private LinkedList<MacroEvent> events;

	public Macro() {
	}

	public Macro(LinkedList<MacroEvent> events) {
		this.events = events;
	}

	/**
	 * Getter for the name of the Macro
	 * 
	 * @return Name of the Macro
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter for the file path of the Macro
	 * 
	 * @return File path of the Macro
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Getter for the events in the Macro
	 * 
	 * @return LinkedList of events
	 */
	public LinkedList<MacroEvent> getEvents() {
		return events;
	}

	/**
	 * Setter for the name of the Macro
	 * 
	 * @param name Value to set Macro's name to.
	 */
	public void setName(String name) {
		this.name = name;
		MinecraftClient MC = MinecraftClient.getInstance();
		this.filePath = MC.runDirectory + File.separator + "aoba" + File.separator + "macros" + File.separator + name
				+ ".macro";
	}
}
