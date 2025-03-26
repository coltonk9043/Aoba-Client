/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
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
		filePath = MC.runDirectory + File.separator + "aoba" + File.separator + "macros" + File.separator + name
				+ ".macro";
	}
}
