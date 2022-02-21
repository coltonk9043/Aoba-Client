package net.aoba.interfaces;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

public interface IWorld {
	public List<BlockEntityTickInvoker> getBlockEntityTickers();
	
	public Stream<VoxelShape> getBlockCollisionsStream(@Nullable Entity entity,
		Box box);
}
