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

/**
 * A class to represent a Color that iterates.
 */
package net.aoba.gui.colors;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;

public class RainbowColor extends Color implements TickListener {
    public RainbowColor() {
        super(255, 0, 0);
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void OnUpdate(TickEvent event) {
    	this.setHue(((this.getHue() + 1f) % 360));
    }
}
