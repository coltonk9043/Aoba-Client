package net.aoba.utils.player;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Vec3d;

public class InteractionUtils {
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final int OFFHAND = 45;

	public static boolean placeBlock(BlockPos blockPos, Hand hand, boolean swingHand) {
		PlayerInventory inventory = MC.player.getInventory();
		ItemStack itemInHand;
		if (hand == Hand.MAIN_HAND)
			itemInHand = inventory.getSelectedStack();
		else
			itemInHand = inventory.getStack(OFFHAND);

		if (itemInHand.getItem() instanceof BlockItem) {
			Direction side = getPlaceSide(blockPos);
			if (side == null)
				return false;

			BlockPos neighbour = blockPos.offset(side);
			Vec3d placePos = Vec3d.ofCenter(blockPos).add(side.getOffsetX() * 0.5, side.getOffsetY() * 0.5,
					side.getOffsetZ() * 0.5);
			BlockHitResult raytraceResult = new BlockHitResult(placePos, side.getOpposite(), neighbour, false);
			return interactBlock(raytraceResult, hand, swingHand);
		} else
			return false;
	}

	public static boolean interactBlock(BlockHitResult blockHitResult, Hand hand, boolean swingHand) {
		ActionResult result = MC.interactionManager.interactBlock(MC.player, hand, blockHitResult);
		if (result.isAccepted()) {
			if (swingHand)
				MC.player.swingHand(hand);
			else
				MC.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
			return true;
		} else
			return false;
	}

	public static Direction getPlaceSide(BlockPos blockPos) {
		Vec3d lookVec = blockPos.toCenterPos().subtract(MC.player.getEyePos());
		double bestRelevancy = -Double.MAX_VALUE;
		Direction bestSide = null;

		for (Direction side : Direction.values()) {
			BlockPos neighborPos = blockPos.offset(side);
			BlockState blockState = MC.world.getBlockState(neighborPos);

			if (blockState.isAir() || !blockState.getFluidState().isEmpty())
				continue;

			AxisDirection direction = side.getDirection();
			double relevancy = side.getAxis().choose(lookVec.getX(), lookVec.getY(), lookVec.getZ())
					* direction.offset();
			if (relevancy > bestRelevancy) {
				bestRelevancy = relevancy;
				bestSide = side;
			}
		}

		return bestSide;
	}
}
