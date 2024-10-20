package net.aoba.event.listeners;

import net.aoba.event.events.ParticleEvent;

public interface ParticleListener extends AbstractListener {
    public abstract void onParticle(ParticleEvent particleEvent);
}
