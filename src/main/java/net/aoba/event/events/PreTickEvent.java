package net.aoba.event.events;

import java.util.ArrayList;
import java.util.List;
import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.PreTickListener;

public class PreTickEvent extends AbstractEvent{
    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            PreTickListener tickListener = (PreTickListener) listener;
            tickListener.onPreTick(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<PreTickListener> GetListenerClassType() {
        return PreTickListener.class;
    }
}
