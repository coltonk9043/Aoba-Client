package net.aoba.event.listeners;

import net.aoba.event.events.BlockStateEvent;
import net.aoba.event.events.FontChangedEvent;
import net.aoba.event.events.KeyDownEvent;

public interface BlockStateListener extends AbstractListener {
	public abstract void OnBlockStateChanged(BlockStateEvent event);
}
