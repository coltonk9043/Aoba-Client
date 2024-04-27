package net.aoba.event.events;

import java.util.ArrayList;
import java.util.List;
import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.FontChangedListener;

public class FontChangedEvent extends AbstractEvent {
	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for(AbstractListener listener : List.copyOf(listeners)) {
			FontChangedListener fontChangeListener = (FontChangedListener) listener;
			fontChangeListener.OnFontChanged(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<FontChangedListener> GetListenerClassType() {
		return FontChangedListener.class;
	}
}