package net.aoba.module.modules.render;

import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class XRay extends Module {
	public static ArrayList<Block> blocks = new ArrayList<Block>();

	public XRay() {
		this.setName("XRay");
		this.setBind(new KeyBinding("key.xray", GLFW.GLFW_KEY_X, "key.categories.aoba" ));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see ores.");
		initXRay();
	}

	@Override
	public void onDisable() {
		mc.options.gamma = 1;
		mc.worldRenderer.reload();
	}

	@Override
	public void onEnable() {
		mc.options.gamma = 100000;
		mc.worldRenderer.reload();
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
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
		
		
	}

}
