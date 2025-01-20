/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.PlayerDeathListener;

import java.util.ArrayList;
import java.util.List;

public class PlayerDeathEvent extends AbstractEvent {
    public PlayerDeathEvent() {
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            PlayerDeathListener playerDeathListener = (PlayerDeathListener) listener;
            playerDeathListener.onPlayerDeath(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<PlayerDeathListener> GetListenerClassType() {
        return PlayerDeathListener.class;
    }
}