package aoba.main.module.modules.misc;

import org.lwjgl.glfw.GLFW;

import aoba.main.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class FastPlace extends Module {

	public FastPlace() {
		this.setName("FastPlace");
		this.setBind(new KeyBinding("key.fastplace", GLFW.GLFW_KEY_UP, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Places blocks exceptionally fast");
	}

	@Override
	public void onDisable() {
		Minecraft.getInstance().rightClickDelayTimer = 4;
	}

	@Override
	public void onEnable() {
		
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		Minecraft.getInstance().rightClickDelayTimer = 0;
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
