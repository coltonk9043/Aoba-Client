/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers;

import static net.aoba.AobaClient.MC;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class InteractionManager {
	public static void destroyBlock(BlockPos blockPos) {
		MC.player.connection.send(
				new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.NORTH));
		MC.player.connection.send(
				new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.NORTH));
	}

	public static void useItemOnBlock(BlockPos blockPos, InteractionHand hand) {
		Vec3 playerPos = MC.player.position();
		Vec3 lookDirection = new Vec3(blockPos.getX() - playerPos.x, blockPos.getY() - playerPos.y,
				blockPos.getZ() - playerPos.z);

		BlockHitResult rayTrace = new BlockHitResult(playerPos.add(lookDirection), Direction.UP, blockPos, false);
		MC.player.connection.send(new ServerboundUseItemOnPacket(hand, rayTrace, 0));
	}

	public static boolean selectItem(Predicate<ItemStack> condition) {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = MC.player.getInventory().getItem(i);
			if (condition.test(stack)) {
				MC.player.getInventory().setSelectedSlot(i);
				return true;
			}
		}
		return false;
	}
}
