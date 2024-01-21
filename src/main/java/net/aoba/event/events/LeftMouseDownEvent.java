package net.aoba.event.events;

import java.util.ArrayList;
import java.util.List;
import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.LeftMouseDownListener;

public class LeftMouseDownEvent extends AbstractEvent{
	
	double mouseX;
	double mouseY;
	
	public LeftMouseDownEvent(double mouseX2, double mouseY2) {
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
			LeftMouseDownListener mouseLeftClickListener = (LeftMouseDownListener) listener;
			mouseLeftClickListener.OnLeftMouseDown(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<LeftMouseDownListener> GetListenerClassType() {
		return LeftMouseDownListener.class;
	}
}