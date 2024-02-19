package net.aoba.event.listeners;

import net.aoba.event.events.ReceivePacketEvent;

public interface ReceivePacketListener extends AbstractListener {
	public abstract void OnReceivePacket(ReceivePacketEvent readPacketEvent);
}
