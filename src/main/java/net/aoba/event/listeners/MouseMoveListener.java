package net.aoba.event.listeners;

import net.aoba.event.events.MouseMoveEvent;

public interface MouseMoveListener extends AbstractListener {
	public abstract void OnMouseMove(MouseMoveEvent mouseMoveEvent);
}
