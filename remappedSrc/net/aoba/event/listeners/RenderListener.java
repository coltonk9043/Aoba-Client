package net.aoba.event.listeners;

import net.aoba.event.events.RenderEvent;

public interface RenderListener extends AbstractListener {
	public abstract void OnRender(RenderEvent event);
}
