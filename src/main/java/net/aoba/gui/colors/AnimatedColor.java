package net.aoba.gui.colors;

import net.aoba.Aoba;
import net.aoba.event.events.PostTickEvent;
import net.aoba.event.listeners.PostTickListener;

public abstract class AnimatedColor extends Color implements PostTickListener {
    public AnimatedColor() {
        super(255, 0, 0);
        Aoba.getInstance().eventManager.AddListener(PostTickListener.class, this);
    }

    @Override
    public abstract void onPostTick(PostTickEvent event);
}
