package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.SendMovementPacketListener;

import java.util.ArrayList;
import java.util.List;

public class SendMovementPacketEvent {
    public static class Pre extends AbstractEvent {
        @Override
        public void Fire(ArrayList<? extends AbstractListener> listeners) {
            for (AbstractListener listener : List.copyOf(listeners)) {
                SendMovementPacketListener sendMovementPacketListener = (SendMovementPacketListener) listener;
                sendMovementPacketListener.onSendMovementPacket(this);
            }
        }

        @Override
        public Class<SendMovementPacketListener> GetListenerClassType() {
            return SendMovementPacketListener.class;
        }
    }

    public static class Post extends AbstractEvent {
        @Override
        public void Fire(ArrayList<? extends AbstractListener> listeners) {
            for (AbstractListener listener : List.copyOf(listeners)) {
                SendMovementPacketListener sendMovementPacketListener = (SendMovementPacketListener) listener;
                sendMovementPacketListener.onSendMovementPacket(this);
            }
        }

        @Override
        public Class<SendMovementPacketListener> GetListenerClassType() {
            return SendMovementPacketListener.class;
        }
    }
}
