package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

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
		//for (BlockEntity entity : mc.world.loadedTileEntityList) {
		//	if(entity instanceof MobSpawnerBlockEntity) {
		//		this.getRenderUtils().TileEntityESPBox(entity, 0.0f, 1.0f, 0.0f);
		//	}
		//}
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
		
		
	}

}