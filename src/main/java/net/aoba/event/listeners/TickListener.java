package net.aoba.event.listeners;

import net.aoba.event.events.TickEvent;

public interface TickListener extends AbstractListener {
	public abstract void OnUpdate(TickEvent event);
}