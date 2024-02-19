package net.aoba.event.listeners;

import net.aoba.event.events.SendPacketEvent;

public interface SendPacketListener extends AbstractListener {
	public abstract void OnSendPacket(SendPacketEvent event);
}
