package net.aoba.gui.colors;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;

public class RandomColor extends Color implements TickListener {
    public RandomColor() {
        super(0, 0, 0);
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void OnUpdate(TickEvent event) {
        this.setHSV(((float) (Math.random() * 360f)), 1f, 1f);
    }
}
