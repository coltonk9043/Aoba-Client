package net.aoba.module.modules.world;

import org.lwjgl.glfw.GLFW;

import net.aoba.gui.Color;
import net.aoba.module.Module;
import net.aoba.settings.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class Nuker extends Module {
	private MinecraftClient mc;
	
	private SliderSetting radius;

	public Nuker() {
		this.setName("Nuker");
		this.setBind(new KeyBinding("key.nuker", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.World);
		this.setDescription("Destroys blocks around the player.");
		this.radius = new SliderSetting("Radius", "nuker_radius", 5f, 0f, 15f, 1f);
		this.addSetting(radius);
		mc = MinecraftClient.getInstance();
	}

	public void setRadius(int radius) {
		this.radius.setValue(radius);
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
		
		int rad = radius.getValueInt();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos(mc.player.getBlockX() + x, (int) mc.player.getBlockY() + y,
							(int) mc.player.getBlockZ() + z);
					Block block = mc.world.getBlockState(blockpos).getBlock();
					if (block == Blocks.AIR)
						continue;
					
					mc.player.networkHandler.sendPacket(
							new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, blockpos, Direction.NORTH));
					mc.player.networkHandler
							.sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, blockpos, Direction.NORTH));
				}
			}
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		int rad = radius.getValueInt();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos(mc.player.getBlockX()+ x, mc.player.getBlockY() + y,
							mc.player.getBlockZ()+ z);
					Block block = mc.world.getBlockState(blockpos).getBlock();

					if (block == Blocks.AIR || block == Blocks.WATER || block == Blocks.LAVA)
						continue;

					this.getRenderUtils().draw3DBox(matrixStack, new Box(blockpos), new Color(255,0,0), 0.2f);
				}
			}
		}
	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
}
