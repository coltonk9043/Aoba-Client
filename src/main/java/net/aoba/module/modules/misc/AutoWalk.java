package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class AutoWalk extends Module {
	public AutoWalk() {
		this.setName("AutoWalk");
		this.setBind(new KeyBinding("key.autowalk", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Places blocks exceptionally fast");
	}

	@Override
	public void onDisable() {
		MC.options.forwardKey.setPressed(false);
	}

	@Override
	public void onEnable() {
		
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		MC.options.forwardKey.setPressed(true);
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
