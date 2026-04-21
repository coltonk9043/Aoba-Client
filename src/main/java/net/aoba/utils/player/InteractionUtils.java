package net.aoba.utils.player;

import net.aoba.Aoba;
import net.aoba.event.events.StartAttackEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class InteractionUtils {
	private static final Minecraft MC = Minecraft.getInstance();
	public static final int OFFHAND = 45;

	public static boolean placeBlock(BlockPos blockPos, InteractionHand hand, boolean swingHand) {
		Inventory inventory = MC.player.getInventory();
		ItemStack itemInHand;
		if (hand == InteractionHand.MAIN_HAND)
			itemInHand = inventory.getSelectedItem();
		else
			itemInHand = inventory.getItem(OFFHAND);

		if (itemInHand.getItem() instanceof BlockItem) {
			Direction side = getPlaceSide(blockPos);
			if (side == null)
				return false;

			BlockPos neighbour = blockPos.relative(side);
			Vec3 placePos = Vec3.atCenterOf(blockPos).add(side.getStepX() * 0.5, side.getStepY() * 0.5,
					side.getStepZ() * 0.5);
			BlockHitResult raytraceResult = new BlockHitResult(placePos, side.getOpposite(), neighbour, false);
			return interactBlock(raytraceResult, hand, swingHand);
		} else
			return false;
	}

	public static boolean interactBlock(BlockHitResult blockHitResult, InteractionHand hand, boolean swingHand) {
		InteractionResult result = MC.gameMode.useItemOn(MC.player, hand, blockHitResult);
		if (result.consumesAction()) {
			if (swingHand)
				MC.player.swing(hand);
			else
				MC.getConnection().send(new ServerboundSwingPacket(hand));
			return true;
		} else
			return false;
	}

	public static void attack(Entity entity) {
		
		// Fire onAttackStart event to simulate player attacking normally.
		// Allows us to inject actions between hits such as criticals, or shield breaker.
    	StartAttackEvent event = new StartAttackEvent(entity);
    	Aoba.getInstance().eventManager.Fire(event);
    	
    	if(event.isCancelled()) {
            return;
    	}
		
		MC.gameMode.attack(MC.player, entity);
		MC.player.swing(InteractionHand.MAIN_HAND);
	}
	
	public static Direction getPlaceSide(BlockPos blockPos) {
		Vec3 lookVec = blockPos.getCenter().subtract(MC.player.getEyePosition());
		double bestRelevancy = -Double.MAX_VALUE;
		Direction bestSide = null;

		for (Direction side : Direction.values()) {
			BlockPos neighborPos = blockPos.relative(side);
			BlockState blockState = MC.level.getBlockState(neighborPos);

			if (blockState.isAir() || !blockState.getFluidState().isEmpty())
				continue;

			AxisDirection direction = side.getAxisDirection();
			double relevancy = side.getAxis().choose(lookVec.x(), lookVec.y(), lookVec.z())
					* direction.getStep();
			if (relevancy > bestRelevancy) {
				bestRelevancy = relevancy;
				bestSide = side;
			}
		}

		return bestSide;
	}
}
