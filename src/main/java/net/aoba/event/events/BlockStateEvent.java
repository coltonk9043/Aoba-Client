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
import net.aoba.event.listeners.BlockStateListener;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockStateEvent extends AbstractEvent {
	private final BlockPos blockPos;
	private final BlockState blockState;
	private final BlockState previousBlockState;

	public BlockStateEvent(BlockPos blockPos, BlockState state, BlockState previousState) {
		this.blockPos = blockPos;
		blockState = state;
		previousBlockState = previousState;
	}

	public BlockPos getBlockPos() {
		return blockPos;
	}

	public BlockState getBlockState() {
		return blockState;
	}

	public BlockState getPreviousBlockState() {
		return previousBlockState;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			BlockStateListener blockStateListener = (BlockStateListener) listener;
			blockStateListener.onBlockStateChanged(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<BlockStateListener> GetListenerClassType() {
		return BlockStateListener.class;
	}
}