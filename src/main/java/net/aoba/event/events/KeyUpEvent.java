package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.KeyUpListener;

import java.util.ArrayList;
import java.util.List;

public class KeyUpEvent extends AbstractEvent {
    private final long window;
    private final int key;
    private final int scancode;
    private final int action;
    private final int modifiers;

    public KeyUpEvent(long window, int key, int scancode, int action, int modifiers) {
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
            KeyUpListener keyDownListener = (KeyUpListener) listener;
            keyDownListener.onKeyUp(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<KeyUpListener> GetListenerClassType() {
        return KeyUpListener.class;
    }
}