/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import java.util.ArrayList;
import java.util.List;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.TickListener;

public class TickEvent {
	public static class Pre extends AbstractEvent {
		@Override
		public void Fire(ArrayList<? extends AbstractListener> listeners) {
			for (AbstractListener listener : List.copyOf(listeners)) {
				TickListener tickListener = (TickListener) listener;
				tickListener.onTick(this);
			}
		}

		@Override
		public Class<TickListener> GetListenerClassType() {
			return TickListener.class;
		}
	}

	public static class Post extends AbstractEvent {
		@Override
		public void Fire(ArrayList<? extends AbstractListener> listeners) {
			for (AbstractListener listener : listeners) {
				TickListener tickListener = (TickListener) listener;
				tickListener.onTick(this);
			}
		}

		@Override
		public Class<TickListener> GetListenerClassType() {
			return TickListener.class;
		}
	}
}
