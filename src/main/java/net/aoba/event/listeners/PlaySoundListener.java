package net.aoba.event.listeners;


import net.aoba.event.events.PlaySoundEvent;

public interface PlaySoundListener extends AbstractListener {
    public abstract void onPlaySound(PlaySoundEvent playSoundEvent);
}
