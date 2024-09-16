package net.aoba.gui.colors;

import net.aoba.event.events.PostTickEvent;

public class RandomColor extends AnimatedColor{
    public RandomColor() {
        super();
    }

    @Override
    public void onPostTick(PostTickEvent event) {
        this.setHSV(((float) (Math.random() * 360f)), 1f, 1f);
    }
}
