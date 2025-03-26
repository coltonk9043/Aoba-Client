/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.colors;

import net.aoba.event.events.TickEvent;

public class RandomColor extends AnimatedColor{
    public RandomColor() {
    }

    @Override
    public void onTick(TickEvent.Post event) {
        setHSV(((float) (Math.random() * 360f)), 1f, 1f);
    }
}
