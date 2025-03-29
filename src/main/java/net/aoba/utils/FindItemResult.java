/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils;

import static net.aoba.AobaClient.MC;

import net.minecraft.util.Hand;

public record FindItemResult(int slot, int count) {
	public boolean found() {
		return slot != -1;
	}

	public Hand getHand() {
		if (slot == 45)
			return Hand.OFF_HAND;
		if (slot == MC.player.getInventory().getSelectedSlot())
			return Hand.MAIN_HAND;
		return null;
	}

	public boolean isMainHand() {
		return getHand() == Hand.MAIN_HAND;
	}

	public boolean isOffhand() {
		return getHand() == Hand.OFF_HAND;
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
