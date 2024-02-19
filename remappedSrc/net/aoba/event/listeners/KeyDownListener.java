package net.aoba.event.listeners;

import net.aoba.event.events.KeyDownEvent;

public interface KeyDownListener extends AbstractListener {
	public abstract void OnKeyDown(KeyDownEvent event);
}
