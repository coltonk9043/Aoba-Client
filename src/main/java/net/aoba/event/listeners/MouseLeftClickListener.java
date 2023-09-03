package net.aoba.event.listeners;

import net.aoba.event.events.MouseLeftClickEvent;

public interface MouseLeftClickListener extends AbstractListener {
	public abstract void OnMouseLeftClick(MouseLeftClickEvent event);
}
