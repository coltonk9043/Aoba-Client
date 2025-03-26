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
import net.aoba.event.listeners.MouseMoveListener;

public class MouseMoveEvent extends AbstractEvent {
	private final double x;
	private final double y;
	private final double deltaX;
	private final double deltaY;

	public MouseMoveEvent(double x, double y, double deltaX, double deltaY) {
        this.x = x;
		this.y = y;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}

	public double getDeltaX() {
		return deltaX;
	}

	public double getDeltaY() {
		return deltaY;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			MouseMoveListener mouseMoveListener = (MouseMoveListener) listener;
			mouseMoveListener.onMouseMove(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<MouseMoveListener> GetListenerClassType() {
		return MouseMoveListener.class;
	}
}