package net.aoba.event.listeners;

import net.aoba.event.events.MouseClickEvent;

public interface MouseClickListener extends AbstractListener {
    public abstract void onMouseClick(MouseClickEvent mouseClickEvent);
}
