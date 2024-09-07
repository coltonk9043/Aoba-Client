package net.aoba.gui.colors;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;

public class AnimatedColor extends Color implements TickListener {
    public AnimatedColor() {
        super(255, 0, 0);
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void OnUpdate(TickEvent event) {
    	this.setHue(((this.getHue() + 1f) % 360));
    }
}
