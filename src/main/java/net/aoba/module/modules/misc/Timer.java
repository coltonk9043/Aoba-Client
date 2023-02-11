package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Timer extends Module {
	private SliderSetting multiplier;
	
	public Timer() {
		this.setName("Timer");
		this.setBind(new KeyBinding("key.timer", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Increases the speed of Minecraft.");
		this.multiplier = new SliderSetting("Multiplier", "timer_multiplier", 1f, 0.1f, 15f, 0.1f);
		this.addSetting(multiplier);
	}

	public float getMultiplier() {
		return this.multiplier.getValueFloat();
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