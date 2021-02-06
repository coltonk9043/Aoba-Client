package aoba.main.module.modules.misc;

import org.lwjgl.glfw.GLFW;

import aoba.main.module.Module;
import aoba.main.module.Module.Category;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

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
	public void onRender() {
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {
	}

	@Override
	public void onReceivePacket(IPacket<?> packet) {
	}
}