/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.event;

import net.aoba.event.events.AbstractEvent;
import net.aoba.event.listeners.AbstractListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {

    private final ConcurrentHashMap<Class<AbstractListener>, ArrayList<AbstractListener>> listeners;

    public EventManager() {
        listeners = new ConcurrentHashMap<Class<AbstractListener>, ArrayList<AbstractListener>>();
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractListener> void AddListener(Class<T> object, AbstractListener listener) {
        try {
            ArrayList<AbstractListener> listOfListeners = listeners.get(object);
            if (listOfListeners == null) {
                listOfListeners = new ArrayList<>(Arrays.asList(listener));
                listeners.put((Class<AbstractListener>) object, listOfListeners);
            } else {
                listOfListeners.add(listener);
            }
        } catch (Exception e) {
            System.out.println("Issue adding listener: " + object.getTypeName() + "...");
            e.printStackTrace();
        }
    }

    public <T extends AbstractListener> void RemoveListener(Class<T> object, AbstractListener listener) {
        try {
            ArrayList<AbstractListener> listOfListeners = listeners.get(object);
            if (listOfListeners != null) {
                listOfListeners.remove(listener);
            }
        } catch (Exception e) {
            System.out.println("Issue removing listener: " + object.getTypeName() + "...");
            e.printStackTrace();
        }
    }

    public void Fire(AbstractEvent event) {
        ArrayList<? extends AbstractListener> listOfListeners = listeners.get(event.GetListenerClassType());

        if (listOfListeners == null) {
            return;
        }

        event.Fire(listOfListeners);
    }
}
