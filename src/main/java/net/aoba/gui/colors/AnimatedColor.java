/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.colors;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;

public abstract class AnimatedColor extends Color implements TickListener {
    public AnimatedColor() {
        super(255, 0, 0);
        Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onTick(TickEvent.Pre event) { }
    
    @Override
    public abstract void onTick(TickEvent.Post event);
}
