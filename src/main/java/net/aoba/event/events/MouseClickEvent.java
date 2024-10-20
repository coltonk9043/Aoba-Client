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
    public final int button;
    public final int action;
    public final int buttonNumber;
    public final int mods;

    public MouseClickEvent(double mouseX, double mouseY, int button, int action, int mods) {
        super();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
        this.action = action;
        this.buttonNumber = -1;
        this.mods = mods;
    }

    public MouseClickEvent(double mouseX, double mouseY, int button, int action, int mods, int buttonNumber) {
        super();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
        this.action = action;
        this.mods = mods;
        this.buttonNumber = buttonNumber;
    }


    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            MouseClickListener mouseClickListener = (MouseClickListener) listener;
            mouseClickListener.onMouseClick(this);
            
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