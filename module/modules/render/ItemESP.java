package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.Packet;

public class ItemESP extends Module {

	public ItemESP() {
		this.setName("ItemESP");
		this.setBind(new KeyBinding("key.itemesp", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see items with an ESP.");

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
		for (Entity entity : mc.world.getEntities()) {
			if(entity instanceof ItemEntity) {
				this.getRenderUtils().EntityESPBox(entity, 255, 0, 255);
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
