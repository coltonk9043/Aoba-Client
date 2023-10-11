package net.aoba.event.events;

import java.util.ArrayList;
import java.util.List;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.event.listeners.LeftMouseUpListener;

public class LeftMouseUpEvent extends AbstractEvent{
	
	double mouseX;
	double mouseY;
	
	public LeftMouseUpEvent(double mouseX2, double mouseY2) {
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
		for(AbstractListener listener : List.copyOf(listeners)) {
			LeftMouseUpListener mouseLeftClickListener = (LeftMouseUpListener) listener;
			mouseLeftClickListener.OnLeftMouseUp(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<LeftMouseUpListener> GetListenerClassType() {
		return LeftMouseUpListener.class;
	}
}