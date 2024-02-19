package net.aoba.event.events;

import java.util.ArrayList;
import java.util.List;
import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.SendPacketListener;
import net.minecraft.network.packet.Packet;

public class SendPacketEvent extends AbstractEvent {

	private Packet<?> packet;
	
	public SendPacketEvent(Packet<?> packet) {
		this.packet = packet;
	}

	public Packet<?> GetPacket(){
		return packet;
	}
	
	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for(AbstractListener listener : List.copyOf(listeners)) {
			SendPacketListener sendPacketListener = (SendPacketListener) listener;
			sendPacketListener.OnSendPacket(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<SendPacketListener> GetListenerClassType() {
		return SendPacketListener.class;
	}
}
