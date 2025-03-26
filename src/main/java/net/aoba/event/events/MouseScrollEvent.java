/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import java.util.ArrayList;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.MouseScrollListener;

public class MouseScrollEvent extends AbstractEvent {
	private final double horizontal;
	private final double vertical;

	public MouseScrollEvent(double horizontal2, double vertical2) {
        horizontal = horizontal2;
		vertical = vertical2;
	}

	public double GetVertical() {
		return vertical;
	}

	public double GetHorizontal() {
		return horizontal;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			MouseScrollListener mouseScrollListener = (MouseScrollListener) listener;
			mouseScrollListener.onMouseScroll(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<MouseScrollListener> GetListenerClassType() {
		return MouseScrollListener.class;
	}
}