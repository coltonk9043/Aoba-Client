package aoba.main.module.modules.render;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class Trajectory extends Module {

	public Trajectory() {
		this.setName("Trajectory");
		this.setBind(new KeyBinding("key.trajectory", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see where they are aiming. (DISABLED)");
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