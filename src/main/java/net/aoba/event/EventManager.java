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

import com.mojang.logging.LogUtils;
import net.aoba.event.events.AbstractEvent;
import net.aoba.event.listeners.AbstractListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
            LogUtils.getLogger().error("Issue adding listener: " + object.getTypeName() + "...");
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
            LogUtils.getLogger().error("Issue removing listener: " + object.getTypeName() + "...");
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

    /**
     * Checks if a specific listener is registered for a given event type.
     *
     * @param object   The class of the listener.
     * @param listener The listener to check.
     * @return True if the listener is registered, false otherwise.
     */
    public <T extends AbstractListener> boolean isListenerRegistered(Class<T> object, AbstractListener listener) {
        ArrayList<AbstractListener> listOfListeners = listeners.get(object);
        return listOfListeners != null && listOfListeners.contains(listener);
    }

    /**
     * Gets all registered listeners for a specific event type.
     *
     * @param object The class of the listener.
     * @return A list of registered listeners for the specified event type.
     */
    public <T extends AbstractListener> List<AbstractListener> getListeners(Class<T> object) {
        return listeners.getOrDefault(object, new ArrayList<>());
    }

    /**
     * Removes all listeners for a specific event type.
     *
     * @param object The class of the listener.
     */
    public <T extends AbstractListener> void clearListeners(Class<T> object) {
        listeners.remove(object);
    }

    /**
     * Removes all listeners for all event types.
     */
    public void clearAllListeners() {
        listeners.clear();
    }

    /**
     * Gets the number of registered listeners for a specific event type.
     *
     * @param object The class of the listener.
     * @return The number of registered listeners for the specified event type.
     */
    public <T extends AbstractListener> int getListenerCount(Class<T> object) {
        ArrayList<AbstractListener> listOfListeners = listeners.get(object);
        return listOfListeners == null ? 0 : listOfListeners.size();
    }

    /**
     * Checks if any listeners are registered for a specific event type.
     *
     * @param object The class of the listener.
     * @return True if any listeners are registered, false otherwise.
     */
    public <T extends AbstractListener> boolean hasListeners(Class<T> object) {
        ArrayList<AbstractListener> listOfListeners = listeners.get(object);
        return listOfListeners != null && !listOfListeners.isEmpty();
    }

    /**
     * Gets all registered event types.
     *
     * @return A set of all registered event types.
     */
    public ConcurrentHashMap.KeySetView<Class<AbstractListener>, ArrayList<AbstractListener>> getAllEventTypes() {
        return listeners.keySet();
    }

    /**
     * Gets registration information for a specific listener.
     *
     * @param listener The listener to get information for.
     * @return A string containing registration information.
     */
    public String getListenerInfo(AbstractListener listener) {
        for (Map.Entry<Class<AbstractListener>, ArrayList<AbstractListener>> entry : listeners.entrySet()) {
            Class<? extends AbstractListener> eventType = entry.getKey();
            ArrayList<AbstractListener> listOfListeners = entry.getValue();
            if (listOfListeners.contains(listener)) {
                return "Listener: " + listener.getClass().getTypeName() + ", Event Type: " + eventType.getTypeName();
            }
        }
        return "Listener not registered.";
    }

    /**
     * Gets all event types that a specific listener is registered for.
     *
     * @param listener The listener to check.
     * @return A list of event types the listener is registered for.
     */
    public List<Class<? extends AbstractListener>> getEventTypesForListener(AbstractListener listener) {
        List<Class<? extends AbstractListener>> eventTypes = new ArrayList<>();
        for (Map.Entry<Class<AbstractListener>, ArrayList<AbstractListener>> entry : listeners.entrySet()) {
            Class<? extends AbstractListener> eventType = entry.getKey();
            ArrayList<AbstractListener> listOfListeners = entry.getValue();
            if (listOfListeners.contains(listener)) {
                eventTypes.add(eventType);
            }
        }
        return eventTypes;
    }
}
