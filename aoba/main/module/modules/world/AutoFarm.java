package aoba.main.module.modules.world;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import aoba.main.module.Module;
import aoba.main.module.Module.Category;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket.Action;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class AutoFarm extends Module {
	private Minecraft mc;
	private int radius = 5;

	public AutoFarm() {
		this.setName("AutoFarm");
		this.setBind(new KeyBinding("key.autofarm", GLFW.GLFW_KEY_RIGHT, "key.categories.aoba"));
		this.setCategory(Category.World);
		this.setDescription("Destroys blocks that can be instantly broken around the player.");
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onToggle() {
	}

	@Override
	public void onUpdate() {
		mc = Minecraft.getInstance();
		for (int x = -radius; x < radius; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -radius; z < radius; z++) {
					BlockPos blockpos = new BlockPos((int) mc.player.getPosX() + x, (int) mc.player.getPosY() + y,
							(int) mc.player.getPosZ() + z);
					Block block = mc.world.getBlockState(blockpos).getBlock();
					BlockState blockstate = mc.world.getBlockState(blockpos);
					if (block instanceof CropsBlock) {
						CropsBlock crop = (CropsBlock) block;
						if (!crop.canGrow(mc.world, blockpos, blockstate, true)) {
							mc.player.connection.sendPacket(
									new CPlayerDiggingPacket(Action.START_DESTROY_BLOCK, blockpos, Direction.NORTH));
							mc.player.connection.sendPacket(
									new CPlayerDiggingPacket(Action.STOP_DESTROY_BLOCK, blockpos, Direction.NORTH));
						}
					}
				}
			}
		}
	}

	@Override
	public void onRender() {
		mc = Minecraft.getInstance();
		for (int x = -radius; x < radius; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -radius; z < radius; z++) {
					BlockPos blockpos = new BlockPos((int) mc.player.getPosX() + x, (int) mc.player.getPosY() + y,
							(int) mc.player.getPosZ() + z);
					Block block = mc.world.getBlockState(blockpos).getBlock();
					if (block instanceof CropsBlock) {
						CropsBlock crop = (CropsBlock) block;
						if (crop.isMaxAge(crop.getDefaultState())) {
							this.getRenderUtils().BlockESPBox(blockpos, 1f, 0, 0);
						}
					}
				}
			}
		}
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {

	}

	@Override
	public void onReceivePacket(IPacket<?> packet) {

	}
}