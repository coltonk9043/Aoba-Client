package net.aoba.managers;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

import static net.aoba.AobaClient.MC;

public class InteractionManager
{
    public InteractionManager()
    {
    }

    public static void destroyBlock(BlockPos blockPos)
    {
        MC.player.networkHandler.sendPacket(
                new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.NORTH));
        MC.player.networkHandler.sendPacket(
                new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.NORTH));
    }

    public static void useItemOnBlock(BlockPos blockPos, Hand hand)
    {
        Vec3d playerPos = MC.player.getPos();
        Vec3d lookDirection = new Vec3d(blockPos.getX() - playerPos.x, blockPos.getY() - playerPos.y, blockPos.getZ() - playerPos.z);

        BlockHitResult rayTrace = new BlockHitResult(playerPos.add(lookDirection), Direction.UP, blockPos, false);
        MC.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, rayTrace, 0));
    }


    public static boolean selectItem(Predicate<ItemStack> condition)
    {
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = MC.player.getInventory().getStack(i);
            if (condition.test(stack))
            {
                MC.player.getInventory().selectedSlot = i;
                return true;
            }
        }
        return false;
    }
}
