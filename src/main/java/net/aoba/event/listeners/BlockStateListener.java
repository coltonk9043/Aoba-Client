package net.aoba.event.listeners;

import net.aoba.event.events.BlockStateEvent;

public interface BlockStateListener extends AbstractListener {
    public abstract void onBlockStateChanged(BlockStateEvent event);
}
