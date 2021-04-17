// Classes Modified:
// - Block.java
// - 

package aoba.main.module.modules.render;

import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.block.Blocks;

public class XRay extends Module {
	public static ArrayList<Block> blocks = new ArrayList<Block>();

	public XRay() {
		this.setName("XRay");
		this.setBind(new KeyBinding("key.xray", GLFW.GLFW_KEY_X, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see ores.");
		initXRay();
	}

	@Override
	public void onDisable() {
		mc.gameSettings.gamma = 1;
		mc.worldRenderer.loadRenderers();
	}

	@Override
	public void onEnable() {
		mc.gameSettings.gamma = 100000;
		mc.worldRenderer.loadRenderers();
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {

	}

	public void initXRay() {
		blocks.add(Blocks.EMERALD_ORE);
		blocks.add(Blocks.EMERALD_BLOCK);
		blocks.add(Blocks.DIAMOND_ORE);
		blocks.add(Blocks.DIAMOND_BLOCK);
		blocks.add(Blocks.GOLD_ORE);
		blocks.add(Blocks.GOLD_BLOCK);
		blocks.add(Blocks.IRON_ORE);
		blocks.add(Blocks.IRON_BLOCK);
		blocks.add(Blocks.COAL_ORE);
		blocks.add(Blocks.COAL_BLOCK);
		blocks.add(Blocks.REDSTONE_BLOCK);
		blocks.add(Blocks.REDSTONE_ORE);
		blocks.add(Blocks.LAPIS_ORE);
		blocks.add(Blocks.LAPIS_BLOCK);
		blocks.add(Blocks.NETHER_QUARTZ_ORE);
		blocks.add(Blocks.MOSSY_COBBLESTONE);
		blocks.add(Blocks.COBBLESTONE);
		blocks.add(Blocks.STONE_BRICKS);
		blocks.add(Blocks.OAK_PLANKS);
	}


	public static boolean isXRayBlock(Block b) {
		if (XRay.blocks.contains(b)) {
			return true;
		}
		return false;
	}

	@Override
	public void onRender() {
		
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {
		
	}

	@Override
	public void onReceivePacket(IPacket<?> packet) {
		
		
	}

}
