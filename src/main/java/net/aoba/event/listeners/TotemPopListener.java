package net.aoba.event.listeners;


import net.aoba.event.events.TotemPopEvent;

public interface TotemPopListener extends AbstractListener {
    public abstract void onTotemPop(TotemPopEvent event);
}
