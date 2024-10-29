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
