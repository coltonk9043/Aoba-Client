package aoba.main.module.modules.world;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import aoba.main.settings.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.block.Blocks;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket.Action;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class Nuker extends Module {
	private Minecraft mc;
	
	private SliderSetting radius;

	public Nuker() {
		this.setName("Nuker");
		this.setBind(new KeyBinding("key.nuker", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.World);
		this.setDescription("Destroys blocks around the player.");
		this.radius = new SliderSetting("Radius", "nuker_radius", 5f, 0f, 15f, 1f);
		this.addSetting(radius);
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
		mc = Minecraft.getInstance();
		int rad = radius.getValueInt();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos((int) mc.player.getPosX() + x, (int) mc.player.getPosY() + y,
							(int) mc.player.getPosZ() + z);
					Block block = mc.world.getBlockState(blockpos).getBlock();
					if (block == Blocks.AIR)
						continue;
					mc.player.connection.sendPacket(
							new CPlayerDiggingPacket(Action.START_DESTROY_BLOCK, blockpos, Direction.NORTH));
					mc.player.connection
							.sendPacket(new CPlayerDiggingPacket(Action.STOP_DESTROY_BLOCK, blockpos, Direction.NORTH));
				}
			}
		}
	}

	@Override
	public void onRender() {
		mc = Minecraft.getInstance();
		int rad = radius.getValueInt();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos((int) mc.player.getPosX() + x, (int) mc.player.getPosY() + y,
							(int) mc.player.getPosZ() + z);
					Block block = mc.world.getBlockState(blockpos).getBlock();

					if (block == Blocks.AIR || block == Blocks.WATER || block == Blocks.LAVA)
						continue;

					this.getRenderUtils().BlockESPBox(blockpos, 1f, 0, 0);
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
