/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.FoodLevelListener;

import java.util.ArrayList;
import java.util.List;

public class FoodLevelEvent extends AbstractEvent {
    private float foodLevel;

    public FoodLevelEvent(float foodLevel) {
        this.foodLevel = foodLevel;
    }

    public float getFoodLevel() {
        return foodLevel;
    }


    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
            FoodLevelListener foodLevelListener = (FoodLevelListener) listener;
            foodLevelListener.onFoodLevelChanged(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<FoodLevelListener> GetListenerClassType() {
        return FoodLevelListener.class;
    }
}