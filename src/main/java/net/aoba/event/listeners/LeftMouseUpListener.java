package net.aoba.event.listeners;

import net.aoba.event.events.LeftMouseUpEvent;

public interface LeftMouseUpListener extends AbstractListener {
	public abstract void OnLeftMouseUp(LeftMouseUpEvent event);
}
