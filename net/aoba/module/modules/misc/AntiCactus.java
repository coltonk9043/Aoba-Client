package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class AntiCactus extends Module {
	
	public AntiCactus() {
		this.setName("AntiCactus");
		this.setBind(new KeyBinding("key.anticactus", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Prevents blocks from hurting you.");
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
	public void onRender(MatrixStack matrixStack) {
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
	}
}