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
import net.aoba.event.listeners.FoodLevelListener;

public class FoodLevelEvent extends AbstractEvent {
	private final float foodLevel;

	public FoodLevelEvent(float foodLevel) {
		this.foodLevel = foodLevel;
	}

	public float getFoodLevel() {
		return foodLevel;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
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