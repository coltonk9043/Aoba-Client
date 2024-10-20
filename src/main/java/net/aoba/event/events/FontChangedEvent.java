package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.FontChangedListener;

import java.util.ArrayList;
import java.util.List;

public class FontChangedEvent extends AbstractEvent {
    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            FontChangedListener fontChangeListener = (FontChangedListener) listener;
            fontChangeListener.onFontChanged(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<FontChangedListener> GetListenerClassType() {
        return FontChangedListener.class;
    }
}