package net.aoba.event.listeners;

import net.aoba.event.events.FontChangedEvent;

public interface FontChangedListener extends AbstractListener {
	public abstract void OnFontChanged(FontChangedEvent event);
}
