/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.listeners;

import net.aoba.event.events.SendMovementPacketEvent;

public interface SendMovementPacketListener extends AbstractListener {
    void onSendMovementPacket(SendMovementPacketEvent.Pre event);
    void onSendMovementPacket(SendMovementPacketEvent.Post event);
}
