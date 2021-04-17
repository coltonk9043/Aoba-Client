package aoba.main.module.modules.world;

import java.util.ArrayList;
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

public class TileBreaker extends Module {
	private Minecraft mc;
	private ArrayList<Block> blocks = new ArrayList<Block>();
	private SliderSetting radius;
	
	public TileBreaker() {
		this.setName("TileBreaker");
		this.setBind(new KeyBinding("key.tilebreaker", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.World);
		this.setDescription("Destroys blocks that can be instantly broken around the player.");
		this.loadTileBreakerBlocks();
		this.radius = new SliderSetting("Radius", "tilebreaker_radius", 5f, 0f, 15f, 1f);
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
		int rad = this.radius.getValueInt();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos((int) mc.player.getPosX() + x,
							(int) mc.player.getPosY() + y,
							(int) mc.player.getPosZ() + z);
					Block block = mc.world.getBlockState(blockpos).getBlock();
					if (this.isTileBreakerBlock(block)) {
						mc.player.connection.sendPacket(
								new CPlayerDiggingPacket(Action.START_DESTROY_BLOCK, blockpos, Direction.NORTH));
						mc.player.connection.sendPacket(
								new CPlayerDiggingPacket(Action.STOP_DESTROY_BLOCK, blockpos, Direction.NORTH));
					}
				}
			}
		}
	}

	@Override
	public void onRender() {
		mc = Minecraft.getInstance();
		int rad = this.radius.getValueInt();
		for (int x = -rad; x < rad; x++) {
			for (int y = rad; y > -rad; y--) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos((int) mc.player.getPosX() + x,
							(int) mc.player.getPosY() + y,
							(int) mc.player.getPosZ() + z);
					Block block = mc.world.getBlockState(blockpos).getBlock();
					if (this.isTileBreakerBlock(block)) {
						this.getRenderUtils().BlockESPBox(blockpos, 1f, 0, 0);
					}
				}
			}
		}
	}
	
	private void loadTileBreakerBlocks() {
		this.blocks.add(Blocks.TORCH);
		this.blocks.add(Blocks.WALL_TORCH);
		this.blocks.add(Blocks.REDSTONE_TORCH);
		this.blocks.add(Blocks.REDSTONE_WALL_TORCH);
		this.blocks.add(Blocks.FERN);
		this.blocks.add(Blocks.LARGE_FERN);
		this.blocks.add(Blocks.FLOWER_POT);
		this.blocks.add(Blocks.POTATOES);
		this.blocks.add(Blocks.CARROTS);
		this.blocks.add(Blocks.WHEAT);
		this.blocks.add(Blocks.BEETROOTS);
		this.blocks.add(Blocks.SUGAR_CANE);
		this.blocks.add(Blocks.GRASS);
		this.blocks.add(Blocks.TALL_GRASS);
		this.blocks.add(Blocks.SEAGRASS);
		this.blocks.add(Blocks.TALL_SEAGRASS);
		this.blocks.add(Blocks.DEAD_BUSH);
		this.blocks.add(Blocks.DANDELION);
		this.blocks.add(Blocks.ROSE_BUSH);
		this.blocks.add(Blocks.POPPY);
		this.blocks.add(Blocks.BLUE_ORCHID);
		this.blocks.add(Blocks.ALLIUM);
		this.blocks.add(Blocks.AZURE_BLUET);
		this.blocks.add(Blocks.RED_TULIP);
		this.blocks.add(Blocks.ORANGE_TULIP);
		this.blocks.add(Blocks.WHITE_TULIP);
		this.blocks.add(Blocks.PINK_TULIP);
		this.blocks.add(Blocks.OXEYE_DAISY);
		this.blocks.add(Blocks.CORNFLOWER);
		this.blocks.add(Blocks.WITHER_ROSE);
		this.blocks.add(Blocks.LILY_OF_THE_VALLEY);
		this.blocks.add(Blocks.BROWN_MUSHROOM);
		this.blocks.add(Blocks.RED_MUSHROOM);
		this.blocks.add(Blocks.SUNFLOWER);
		this.blocks.add(Blocks.LILAC);
		this.blocks.add(Blocks.PEONY);
	}
	
	public boolean isTileBreakerBlock(Block b) {
		if (this.blocks.contains(b)) {
			return true;
		}
		return false;
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {
		
	}

	@Override
	public void onReceivePacket(IPacket<?> packet) {
		
		
	}
}
