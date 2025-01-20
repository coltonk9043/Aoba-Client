/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.MouseMoveListener;

import java.util.ArrayList;
import java.util.List;

public class MouseMoveEvent extends AbstractEvent {
    private double x;
    private double y;
    private double deltaX;
    private double deltaY;
    
    public MouseMoveEvent(double x, double y, double deltaX, double deltaY) {
        super();
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
    	return this.deltaX;
    }
    
    public double getDeltaY() {
    	return this.deltaY;
    }
    
    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
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