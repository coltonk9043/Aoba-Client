package aoba.main.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

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
	public void onRender() {
		
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {
		
	}
	
	@Override
	public void onReceivePacket(IPacket<?> packet) {
		
	}
}