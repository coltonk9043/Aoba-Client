package net.aoba.event.events;

import java.util.ArrayList;
import java.util.List;
import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.MouseScrollListener;

public class MouseScrollEvent extends AbstractEvent{
	private double horizontal;
	private double vertical;
	
	public MouseScrollEvent(double horizontal2, double vertical2) {
		super();
		this.horizontal = horizontal2;
		this.vertical = vertical2;
	}
	
	public double GetVertical() {
		return vertical;
	}
	
	public double GetHorizontal() {
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