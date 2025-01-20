/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.entity;

import static net.aoba.AobaClient.MC;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

public class EntityUtils {
	public static GameMode getGameMode(PlayerEntity player) {
		if (player == null)
			return null;
		PlayerListEntry playerListEntry = MC.getNetworkHandler().getPlayerListEntry(player.getUuid());
		if (playerListEntry == null)
			return null;
		return playerListEntry.getGameMode();
	}
}
