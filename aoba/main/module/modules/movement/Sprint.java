package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class Sprint extends Module {

	public Sprint() {
		this.setName("Sprint");
		this.setBind(new KeyBinding("key.sprinthack", GLFW.GLFW_KEY_G, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Permanently keeps player in sprinting mode.");
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
		mc.player.setSprinting(true);
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
