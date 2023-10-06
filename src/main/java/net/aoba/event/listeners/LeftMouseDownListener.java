package net.aoba.event.listeners;

import net.aoba.event.events.LeftMouseDownEvent;

public interface LeftMouseDownListener extends AbstractListener {
	public abstract void OnLeftMouseDown(LeftMouseDownEvent event);
}
