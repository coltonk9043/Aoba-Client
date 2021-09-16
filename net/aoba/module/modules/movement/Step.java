package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Step extends Module {

	private SliderSetting stepHeight;
	
	public Step() {
		this.setName("Step");
		this.setBind(new KeyBinding("key.step", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Steps up blocks.");
		
		stepHeight = new SliderSetting("Height", "step_height", 1f, 0.0f, 2f, 0.5f);
		this.addSetting(stepHeight);
	}

	@Override
	public void onDisable() {
		if(mc.world != null) {
			mc.player.stepHeight = .5f;
		}
	}

	@Override
	public void onEnable() {
		mc.player.stepHeight = stepHeight.getValueFloat();
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
	
	public void setStepHeight(float height) {
		this.stepHeight.setValue(height);
	}
}
