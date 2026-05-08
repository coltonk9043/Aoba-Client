package net.aoba.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record BlockPlacement(BlockPos targetPos, BlockPos placementPos, Direction placementFace) {
}