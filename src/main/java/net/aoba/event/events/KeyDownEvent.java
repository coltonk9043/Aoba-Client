package net.aoba.event.events;

import java.util.ArrayList;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.KeyDownListener;

public class KeyDownEvent extends AbstractEvent{
	private final long window;
	private final int key;
	private final int scancode;
	private final int action;
	private final int modifiers;
	
	public KeyDownEvent(long window, int key, int scancode, int action, int modifiers) {
		super();
		this.window = window;
		this.key = key;
		this.scancode = scancode;
		this.action = action;
		this.modifiers = modifiers;
	}

	public long GetWindow() {
		return window;
	}
	
	public int GetKey() {
		return key;
	}
	
	public int GetScanCode() {
		return scancode;
	}
	
	public int GetAction() {
		return action;
	}
	
	public int GetModifiers() {
		return modifiers;
	}
	
	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for(AbstractListener listener : listeners) {
			KeyDownListener keyDownListener = (KeyDownListener) listener;
			keyDownListener.OnKeyDown(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<KeyDownListener> GetListenerClassType() {
		return KeyDownListener.class;
	}
}