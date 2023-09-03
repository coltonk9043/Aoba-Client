package net.aoba.event.events;

import java.util.ArrayList;
import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.MouseLeftClickListener;

public class MouseLeftClickEvent extends AbstractEvent{
	
	int mouseX;
	int mouseY;
	
	public MouseLeftClickEvent(int mouseX, int mouseY) {
		super();
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	public int GetMouseX() {
		return mouseX;
	}
	
	public int GetMouseY() {
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