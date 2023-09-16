package net.aoba.event.events;

import java.util.ArrayList;
import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.MouseLeftClickListener;

public class MouseLeftClickEvent extends AbstractEvent{
	
	double mouseX;
	double mouseY;
	
	public MouseLeftClickEvent(double mouseX2, double mouseY2) {
		super();
		this.mouseX = mouseX2;
		this.mouseY = mouseY2;
	}

	public double GetMouseX() {
		return mouseX;
	}
	
	public double GetMouseY() {
		return mouseY;
	}
	
	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for(AbstractListener listener : listeners) {
			MouseLeftClickListener mouseLeftClickListener = (MouseLeftClickListener) listener;
			mouseLeftClickListener.OnMouseLeftClick(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<MouseLeftClickListener> GetListenerClassType() {
		return MouseLeftClickListener.class;
	}
}