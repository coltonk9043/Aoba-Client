package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Nametags extends Module {

	public Nametags() {
		this.setName("Nametags");
		this.setBind(new KeyBinding("key.nametags", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Scales the nametags to be larger.");
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