package net.aoba.event.events;

import java.util.ArrayList;

import net.aoba.event.listeners.AbstractListener;

public abstract class AbstractEvent {
	boolean isCancelled;
	
	public AbstractEvent() {
		isCancelled = false;
	}
	
	public boolean IsCancelled() {
		return isCancelled;
	}
	
	public void SetCancelled(boolean state) {
		this.isCancelled = state;
	}
	
	public abstract void Fire(ArrayList<? extends AbstractListener> listeners);
	public abstract <T extends AbstractListener> Class<T> GetListenerClassType();
}
