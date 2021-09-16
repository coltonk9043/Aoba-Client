package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class AntiKnockback extends Module {
	
	public AntiKnockback() {
		this.setName("AntiKnockback");
		this.setBind(new KeyBinding("key.antiknockback", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Prevents knockback.");
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