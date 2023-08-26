package net.aoba.event.events;

import java.util.ArrayList;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.TickListener;

public class TickEvent extends AbstractEvent {
	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for(AbstractListener listener : listeners) {
			TickListener tickListener = (TickListener) listener;
			tickListener.OnUpdate(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<TickListener> GetListenerClassType() {
		return TickListener.class;
	}
}