/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;

import java.util.ArrayList;

public abstract class AbstractEvent {
    boolean isCancelled;

    public AbstractEvent() {
        isCancelled = false;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        this.isCancelled = true;
    }

    public abstract void Fire(ArrayList<? extends AbstractListener> listeners);

    public abstract <T extends AbstractListener> Class<T> GetListenerClassType();
}
