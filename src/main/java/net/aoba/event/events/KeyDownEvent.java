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

package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.KeyDownListener;

import java.util.ArrayList;
import java.util.List;

public class KeyDownEvent extends AbstractEvent {
    private final long window;
    private final int key;
    private final int scancode;
    private final int action;
    private final int modifiers;

    public KeyDownEvent(long window, int key, int scancode, int action, int modifiers) {
        super();
        this.window = window;
        this.key = key;
        this.scancode = scancode;
        this.action = action;
        this.modifiers = modifiers;
    }

    public long GetWindow() {
        return window;
    }

    public int GetKey() {
        return key;
    }

    public int GetScanCode() {
        return scancode;
    }

    public int GetAction() {
        return action;
    }

    public int GetModifiers() {
        return modifiers;
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            KeyDownListener keyDownListener = (KeyDownListener) listener;
            keyDownListener.onKeyDown(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<KeyDownListener> GetListenerClassType() {
        return KeyDownListener.class;
    }
}