package net.aoba.event.listeners;

import net.aoba.event.events.SendMovementPacketEvent;

public interface SendMovementPacketListener extends AbstractListener {
    public abstract void onSendMovementPacket(SendMovementPacketEvent.Pre event);
    public abstract void onSendMovementPacket(SendMovementPacketEvent.Post event);
}
