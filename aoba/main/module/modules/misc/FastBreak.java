package aoba.main.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class FastBreak extends Module {

	private float multiplier = 1.25f;
	
	public FastBreak() {
		this.setName("FastBreak");
		this.setBind(new KeyBinding("key.fastbreak", GLFW.GLFW_KEY_DOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Breaks blocks quicker based on a multiplier.");
		this.hasSettings = true;
	}

	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}
	
	public float getMultiplier() {
		return this.multiplier;
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
