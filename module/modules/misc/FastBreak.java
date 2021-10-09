package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class FastBreak extends Module {

	private SliderSetting multiplier;
	
	public FastBreak() {
		this.setName("FastBreak");
		this.setBind(new KeyBinding("key.fastbreak", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Breaks blocks quicker based on a multiplier.");
		
		multiplier = new SliderSetting("Multiplier", "fastbreak_multiplier", 1.25f, 1f, 3f, 0.05f);
		this.addSetting(multiplier);
	}

	public void setMultiplier(float multiplier) {
		this.multiplier.setValue(multiplier);
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
