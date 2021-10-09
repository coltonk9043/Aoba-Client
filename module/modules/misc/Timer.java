package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Timer extends Module {
	float multiplier = 0.2f;
	
	public Timer() {
		this.setName("Timer");
		this.setBind(new KeyBinding("key.timer", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Increases the speed of Minecraft.");
	}

	@Override
	public void onDisable() {
		//mc.timer.setMultiplier(1f);
	}

	@Override
	public void onEnable() {
		//mc.timer.setMultiplier(0.5f);
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