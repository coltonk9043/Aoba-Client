/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.listeners;

import net.aoba.event.events.ReceivePacketEvent;

public interface ReceivePacketListener extends AbstractListener {
    void onReceivePacket(ReceivePacketEvent readPacketEvent);
}
