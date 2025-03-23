/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.listeners;

import net.aoba.event.events.ItemUsedEvent;

public interface ItemUsedListener extends AbstractListener {
	public abstract void onItemUsed(ItemUsedEvent.Pre event);

	public abstract void onItemUsed(ItemUsedEvent.Post event);
}
