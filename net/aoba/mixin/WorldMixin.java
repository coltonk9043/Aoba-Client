package net.aoba.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.aoba.interfaces.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess, AutoCloseable, IWorld
{
	@Shadow
	@Final
	protected List<BlockEntityTickInvoker> blockEntityTickers;
	
	@Override
	public List<BlockEntityTickInvoker> getBlockEntityTickers()
	{
		return blockEntityTickers;
	}
}
