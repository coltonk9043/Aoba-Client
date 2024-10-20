package net.aoba.event.listeners;

import net.aoba.event.events.GameLeftEvent;

public interface GameLeftListener extends AbstractListener {
    public abstract void onGameLeft(GameLeftEvent event);
}
