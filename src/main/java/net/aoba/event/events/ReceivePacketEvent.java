/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.ReceivePacketListener;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class ReceivePacketEvent extends AbstractEvent {

    private Packet<?> packet;

    public Packet<?> GetPacket() {
        return packet;
    }

    public ReceivePacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            ReceivePacketListener readPacketListener = (ReceivePacketListener) listener;
            readPacketListener.onReceivePacket(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<ReceivePacketListener> GetListenerClassType() {
        return ReceivePacketListener.class;
    }
}
