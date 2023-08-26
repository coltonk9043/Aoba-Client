package net.aoba.event.events;

import java.util.ArrayList;
import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.ReceivePacketListener;
import net.minecraft.network.packet.Packet;

public class ReceivePacketEvent extends AbstractEvent {

	private Packet<?> packet;
	
	public Packet<?> GetPacket(){
		return packet;
	}
	
	public ReceivePacketEvent(Packet<?> packet) {
		this.packet = packet;
	}
	
	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for(AbstractListener listener : listeners) {
			ReceivePacketListener readPacketListener = (ReceivePacketListener) listener;
			readPacketListener.OnReceivePacket(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<ReceivePacketListener> GetListenerClassType() {
		return ReceivePacketListener.class;
	}
}
