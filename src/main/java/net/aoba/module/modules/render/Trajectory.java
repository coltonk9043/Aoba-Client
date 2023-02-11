package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Trajectory extends Module {

	public Trajectory() {
		this.setName("Trajectory");
		this.setBind(new KeyBinding("key.trajectory", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see where they are aiming. (DISABLED)");
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
		
	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}

}