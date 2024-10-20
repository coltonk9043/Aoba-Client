package net.aoba.macros;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.aoba.Aoba;
import net.aoba.macros.actions.MacroEvent;

public class MacroPlayer {

	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private LinkedList<MacroEvent> currentMacro = new LinkedList<MacroEvent>();
	private boolean isPlaying = false;
	private long startTime = 0;
	private long timeStamp = 0;
	
	public void play(LinkedList<MacroEvent> events) {
		Aoba.getInstance().guiManager.setClickGuiOpen(false);
		
		isPlaying = true;
		startTime = System.currentTimeMillis();
		timeStamp = 0;
		currentMacro = (LinkedList<MacroEvent>)events.clone();
		executor.submit(this::execute);
	}
	
	private void execute() {
		System.out.println("Starting macro");
		System.out.println("Macro Size: " + currentMacro.size());
		MacroEvent event = currentMacro.poll();
		while(event != null) {
			timeStamp = System.currentTimeMillis() - startTime;
			
			if(timeStamp >= event.getTimestamp()) {
				event.execute();
				event = currentMacro.poll();
			}
		}
		System.out.println("Stopped macro");
	}
}
