package net.aoba.event.listeners;

import net.aoba.event.events.MouseScrollEvent;

public interface MouseScrollListener extends AbstractListener {
	public abstract void OnMouseScroll(MouseScrollEvent event);
}
