/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.event.events;

import net.aoba.event.listeners.AbstractListener;
import net.aoba.event.listeners.BlockStateListener;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BlockStateEvent extends AbstractEvent {
    private BlockPos blockPos;
    private BlockState blockState;
    private BlockState previousBlockState;

    public BlockStateEvent(BlockPos blockPos, BlockState state, BlockState previousState) {
        this.blockPos = blockPos;
        this.blockState = state;
        this.previousBlockState = previousState;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public BlockState getPreviousBlockState() {
        return this.previousBlockState;
    }

    @Override
    public void Fire(ArrayList<? extends AbstractListener> listeners) {
        for (AbstractListener listener : List.copyOf(listeners)) {
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