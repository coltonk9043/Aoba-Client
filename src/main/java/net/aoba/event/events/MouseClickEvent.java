package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

import java.util.ArrayList;
import java.util.List;

public class MouseClickEvent extends AbstractEvent {

    public final double mouseX;
    public final double mouseY;
    public final MouseButton button;
    public final MouseAction action;

    public MouseClickEvent(double mouseX, double mouseY, MouseButton button, MouseAction action) {
        super();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
        this.action = action;
    }


    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            MouseClickListener mouseClickListener = (MouseClickListener) listener;
            mouseClickListener.OnMouseClick(this);
            
            if(this.isCancelled)
            	break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<MouseClickListener> GetListenerClassType() {
        return MouseClickListener.class;
    }
}