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
import net.aoba.event.listeners.TotemPopListener;
import net.minecraft.entity.player.PlayerEntity;

public class TotemPopEvent extends AbstractEvent {
	private final PlayerEntity entity;
	private final int pops;

	public TotemPopEvent(PlayerEntity entity, int pops) {
		this.entity = entity;
		this.pops = pops;
	}

	public PlayerEntity getEntity() {
		return entity;
	}

	public int getPops() {
		return pops;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			TotemPopListener totemPopListener = (TotemPopListener) listener;
			totemPopListener.onTotemPop(this);

			if (isCancelled)
				break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<TotemPopListener> GetListenerClassType() {
		return TotemPopListener.class;
	}
}
