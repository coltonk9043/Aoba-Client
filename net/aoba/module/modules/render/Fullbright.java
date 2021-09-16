package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Fullbright extends Module {

	public Fullbright() {
		this.setName("Fullbright");
		this.setBind(new KeyBinding("key.fullbright", GLFW.GLFW_KEY_F, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Maxes out the brightness.");

	}

	@Override
	public void onDisable() {
		mc.options.gamma = 1;
	}

	@Override
	public void onEnable() {
		mc.options.gamma = 100000;
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {

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
