/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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