package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.TotemPopListener;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class TotemPopEvent extends AbstractEvent {
    private final PlayerEntity entity;
    private int pops;

    public TotemPopEvent(PlayerEntity entity, int pops) {
        this.entity = entity;
        this.pops = pops;
    }

    public PlayerEntity getEntity() {
        return this.entity;
    }

    public int getPops() {
        return this.pops;
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            TotemPopListener totemPopListener = (TotemPopListener) listener;
            totemPopListener.onTotemPop(this);

            if(this.isCancelled)
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<TotemPopListener> GetListenerClassType() {
        return TotemPopListener.class;
    }
}
