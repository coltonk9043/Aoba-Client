package net.aoba.event.listeners;

import net.aoba.event.events.KeyUpEvent;

public interface KeyUpListener extends AbstractListener {
    public abstract void onKeyUp(KeyUpEvent event);
}