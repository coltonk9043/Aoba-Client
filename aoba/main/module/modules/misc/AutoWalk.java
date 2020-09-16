package aoba.main.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class AutoWalk extends Module {
	public AutoWalk() {
		this.setName("AutoWalk");
		this.setBind(new KeyBinding("key.autowalk", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Places blocks exceptionally fast");
	}

	@Override
	public void onDisable() {
		mc.gameSettings.keyBindForward.setPressed(false);
	}

	@Override
	public void onEnable() {
		
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		mc.gameSettings.keyBindForward.setPressed(true);
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
