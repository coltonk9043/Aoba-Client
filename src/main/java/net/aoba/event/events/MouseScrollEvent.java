package net.aoba.event.events;

import java.util.ArrayList;
import java.util.List;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.event.listeners.MouseScrollListener;

public class MouseScrollEvent extends AbstractEvent{
	private float horizontal;
	private float vertical;
	
	public MouseScrollEvent(float horizontal, float vertical) {
		super();
		this.horizontal = horizontal;
		this.vertical = vertical;
	}
	
	public float GetVertical() {
		return vertical;
	}
	
	public float GetHorizontal() {
		return horizontal;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for(AbstractListener listener : List.copyOf(listeners)) {
			MouseScrollListener mouseScrollListener = (MouseScrollListener) listener;
			mouseScrollListener.OnMouseScroll(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<MouseScrollListener> GetListenerClassType() {
		return MouseScrollListener.class;
	}
}