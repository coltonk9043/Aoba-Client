package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;

import net.aoba.gui.Color;
import net.aoba.interfaces.IWorld;
import net.aoba.module.Module;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

public class SpawnerESP extends Module {

	public SpawnerESP() {
		this.setName("SpawnerESP");
		this.setBind(new KeyBinding("key.spawneresp", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see spawners with an ESP.");
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

	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		for (BlockEntityTickInvoker tickInvolker : ((IWorld)mc.world).getBlockEntityTickers()) {
			BlockEntity blockEntity = mc.world.getBlockEntity(tickInvolker.getPos());
			if(blockEntity instanceof MobSpawnerBlockEntity) {
				Box box = new Box(blockEntity.getPos());
				this.getRenderUtils().draw3DBox(matrixStack, box, new Color(255,255,0), 0.2f);
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