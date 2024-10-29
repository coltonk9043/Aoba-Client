package net.aoba.macros;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.aoba.Aoba;
import net.aoba.macros.actions.MacroEvent;

public class MacroPlayer {

	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private Macro currentMacro = null;
	private boolean isPlaying = false;
	private long startTime = 0;
	private long timeStamp = 0;
	
	public void play(Macro macro) {
		Aoba.getInstance().guiManager.setClickGuiOpen(false);
		
		isPlaying = true;
		startTime = System.nanoTime();
		timeStamp = 0;
		currentMacro = macro;
		executor.submit(this::execute);
	}
	
	private void execute() {
		LinkedList<MacroEvent> events = (LinkedList<MacroEvent>) currentMacro.getEvents().clone();
		System.out.println("Starting macro");
		System.out.println("Macro Size: " + events.size());
		MacroEvent event = events.poll();
		while(event != null) {
			timeStamp = System.nanoTime() - startTime;
			
			if(timeStamp >= event.getTimestamp()) {
				event.execute();
				event = events.poll();
			}
		}
		System.out.println("Stopped macro");
	}
}
