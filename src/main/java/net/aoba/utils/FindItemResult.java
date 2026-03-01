/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils;

import static net.aoba.AobaClient.MC;

import net.minecraft.world.InteractionHand;

public record FindItemResult(int slot, int count) {
	public boolean found() {
		return slot != -1;
	}

	public InteractionHand getHand() {
		if (slot == 45)
			return InteractionHand.OFF_HAND;
		if (slot == MC.player.getInventory().getSelectedSlot())
			return InteractionHand.MAIN_HAND;
		return null;
	}

	public boolean isMainHand() {
		return getHand() == InteractionHand.MAIN_HAND;
	}

	public boolean isOffhand() {
		return getHand() == InteractionHand.OFF_HAND;
	}

	public boolean isHotbar() {
		return slot >= 0 && slot <= 8;
	}

	public boolean isMain() {
		return slot >= 9 && slot <= 35;
	}

	public boolean isArmor() {
		return slot >= 36 && slot <= 39;
	}
}
