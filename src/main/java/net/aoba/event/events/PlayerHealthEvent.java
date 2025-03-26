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
import net.aoba.event.listeners.PlayerHealthListener;
import net.minecraft.entity.damage.DamageSource;

public class PlayerHealthEvent extends AbstractEvent {
	private final float health;
	private final DamageSource source;

	public PlayerHealthEvent(DamageSource source, float health) {
		this.source = source;
		this.health = health;
	}

	public float getHealth() {
		return health;
	}

	public DamageSource getDamageSource() {
		return source;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			PlayerHealthListener playerHealthListener = (PlayerHealthListener) listener;
			playerHealthListener.onHealthChanged(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<PlayerHealthListener> GetListenerClassType() {
		return PlayerHealthListener.class;
	}
}