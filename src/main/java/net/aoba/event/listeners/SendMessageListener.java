package net.aoba.event.listeners;


import net.aoba.event.events.SendMessageEvent;

public interface SendMessageListener extends AbstractListener {
    public abstract void OnMessage(SendMessageEvent sendMessageEvent);
}
