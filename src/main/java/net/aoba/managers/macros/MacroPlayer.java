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

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.aoba.Aoba;
import net.aoba.managers.macros.actions.MacroEvent;

/**
 * Class responsible for playing back a Macro
 */
public class MacroPlayer {
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();

	private Macro currentMacro = null;
	private boolean isPlaying = false;
	private long startTime = 0;
	private long timeStamp = 0;

	/**
	 * Plays a Macro back in a new Thread.
	 * 
	 * @param macro Macro to play.
	 */
	public void play(Macro macro) {
		Aoba.getInstance().guiManager.setClickGuiOpen(false);

		isPlaying = true;
		startTime = System.nanoTime();
		timeStamp = 0;
		currentMacro = macro;
		executor.submit(this::execute);
	}

	/**
	 * Executes the currently selected Macro.
	 */
	private void execute() {
		// Create a copy of the Events so that we don't affect the original Macro.
		LinkedList<MacroEvent> events = (LinkedList<MacroEvent>) currentMacro.getEvents().clone();

		// Continue playing events until there are none left.
		MacroEvent event = events.poll();
		while (event != null) {
			timeStamp = System.nanoTime() - startTime;

			if (timeStamp >= event.getTimestamp()) {
				event.execute();
				event = events.poll();
			}
		}
	}
}
