package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Sprint extends Module {

	public Sprint() {
		this.setName("Sprint");
		this.setBind(new KeyBinding("key.sprinthack", GLFW.GLFW_KEY_G, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Permanently keeps player in sprinting mode.");
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
		mc.player.setSprinting(true);
	}

	@Override
	public void onRender(MatrixStack matrixStack) {

	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
		
		
	}
}
