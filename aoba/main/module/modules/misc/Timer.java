package aoba.main.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class Timer extends Module {
	float multiplier = 0.2f;
	
	public Timer() {
		this.setName("Timer");
		this.setBind(new KeyBinding("key.timer", GLFW.GLFW_KEY_LEFT, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Increases the speed of Minecraft.");
	}

	@Override
	public void onDisable() {
		mc.timer.setMultiplier(1f);
	}

	@Override
	public void onEnable() {
		mc.timer.setMultiplier(0.5f);
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