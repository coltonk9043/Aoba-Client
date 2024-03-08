package net.aoba.event.listeners;

import net.aoba.event.events.FontChangedEvent;
import net.aoba.event.events.KeyDownEvent;

public interface FontChangedListener extends AbstractListener {
	public abstract void OnFontChanged(FontChangedEvent event);
}
