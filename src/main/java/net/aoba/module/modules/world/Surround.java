/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.world;

import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BlocksSetting;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Surround extends Module implements TickListener {
	public FloatSetting placeHeight = FloatSetting.builder().id("surround_height").displayName("Height")
			.description("Height that surround walls will go.").defaultValue(1f).minValue(1f).maxValue(3f).step(1.0f)
			.build();

	public BlocksSetting blocks = BlocksSetting.builder().id("surround_blocks").displayName("Blocks")
			.description("Blocks that will be used to place surrounding blocks.")
			.defaultValue(new HashSet<Block>(Lists.newArrayList(Blocks.OBSIDIAN, Blocks.ENDER_CHEST,
					Blocks.ENCHANTING_TABLE, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL,
					Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS, Blocks.RESPAWN_ANCHOR)))
			.build();

	public BooleanSetting alignCharacter = BooleanSetting.builder().id("surround_align").displayName("Align")
			.description("Aligns the character to the nearest block.").defaultValue(false).build();

	public BooleanSetting autoDisable = BooleanSetting.builder().id("surround_autodisable").displayName("Auto-Disable")
			.description("Disables the module when the blocks have finished placing.").defaultValue(false).build();

	public BooleanSetting legit = BooleanSetting.builder().id("surround_legit").displayName("Legit")
			.description("Whether or not to simulate a player looking and clicking to place.").defaultValue(false)
			.build();

	private static final List<Block> BREAKABLE_BLOCKS = Lists.newArrayList(Blocks.TALL_GRASS, Blocks.FERN,
			Blocks.LARGE_FERN, Blocks.DEAD_BUSH, Blocks.VINE, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES,
			Blocks.BEETROOTS);

	public Surround() {
		super("Surround");
		this.setCategory(Category.of("World"));
		this.setDescription("Surrounds the player with blocks.");

		this.addSetting(placeHeight);
		this.addSetting(blocks);
		this.addSetting(alignCharacter);
		this.addSetting(autoDisable);
		this.addSetting(legit);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
		if (alignCharacter.getValue()) {
			BlockPos blockPos = MC.player.getBlockPos();
			MC.player.updatePosition(blockPos.getX() + 0.5f, MC.player.getY(), blockPos.getZ() + 0.5f);
			MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(blockPos.getX() + 0.5f,
					MC.player.getY(), blockPos.getZ() + 0.5f, MC.player.isOnGround(), false));
		}
	}

	@Override
	public void onToggle() {

	}

	private int getBlockInventorySlot() {
		HashSet<Block> availableBlocks = blocks.getValue();
		for (int i = 0; i < 36; i++) {
			ItemStack stack = MC.player.getInventory().getStack(i);
			if (stack != null && availableBlocks.contains(Block.getBlockFromItem(stack.getItem()))) {
				return i;
			}
		}
		return -1;
	}

	private void placeBlock(BlockPos pos, Hand hand) {
		for (Direction direction : Direction.values()) {
			BlockPos offsetPos = pos.offset(direction);
			if (!MC.world.isInBuildLimit(offsetPos))
				continue;

			if (MC.world.getBlockState(offsetPos).isSolidBlock(MC.world, offsetPos)) {
				if (legit.getValue()) {
					MC.player.swingHand(hand);
				} else {
					MC.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
				}

				MC.interactionManager.interactBlock(MC.player, hand,
						new BlockHitResult(Vec3d.ofCenter(pos), direction.getOpposite(), pos, false));
				break;
			}
		}
	}

	private void breakBlock(BlockPos pos, Hand hand) {
		MC.interactionManager.attackBlock(pos, Direction.UP);
		if (legit.getValue()) {
			MC.player.swingHand(hand);
		} else {
			MC.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
		}
	}

	@Override
	public void onTick(Pre event) {
		int foundBlockSlot = getBlockInventorySlot();
		int oldSlot = MC.player.getInventory().selectedSlot;

		// Disable the module if no block was found in the inventory.
		if (foundBlockSlot == -1) {
			this.state.setValue(false);
			return;
		}

		// Change the selected slot and determine which hand it is in.
		MC.player.getInventory().selectedSlot = foundBlockSlot;
		MC.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(foundBlockSlot));
		Hand hand = foundBlockSlot == 40 ? Hand.OFF_HAND : Hand.MAIN_HAND;

		// Get the block positions around the player and place the blocks.
		BlockPos playerPosition = MC.player.getBlockPos();
		int height = placeHeight.getValue().intValue();

		for (int i = 0; i < height; i++) {
			BlockPos newPos = playerPosition.add(0, i, 0);
			List<BlockPos> placePositions = Lists.newArrayList(newPos.north(), newPos.east(), newPos.south(),
					newPos.west());
			for (BlockPos pos : placePositions) {
				if (MC.world.getBlockState(pos).isReplaceable()) {
					placeBlock(pos, hand);
				} else if (BREAKABLE_BLOCKS.contains(MC.world.getBlockState(pos).getBlock())) {
					breakBlock(pos, hand);
					placeBlock(pos, hand);
				}
			}
		}

		// Return Selected Slot back to original slot.
		MC.player.getInventory().selectedSlot = oldSlot;
		MC.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(oldSlot));

		// Disable state if auto-disable is enabled.
		if (autoDisable.getValue()) {
			state.setValue(false);
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
