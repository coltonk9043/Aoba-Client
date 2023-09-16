package net.aoba.event.events;

import java.util.ArrayList;
import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.MouseMoveListener;

public class MouseMoveEvent extends AbstractEvent{
	private double horizontal;
	private double vertical;
	
	public MouseMoveEvent(double x, double y) {
		super();
		this.horizontal = x;
		this.vertical = y;
	}
	
	public double GetVertical() {
		return vertical;
	}
	
	public double GetHorizontal() {
		return horizontal;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for(AbstractListener listener : listeners) {
			MouseMoveListener mouseMoveListener = (MouseMoveListener) listener;
			mouseMoveListener.OnMouseMove(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<MouseMoveListener> GetListenerClassType() {
		return MouseMoveListener.class;
	}
}