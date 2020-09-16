package aoba.main.module.modules.render;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class Fullbright extends Module {

	public Fullbright() {
		this.setName("Fullbright");
		this.setBind(new KeyBinding("key.fullbright", GLFW.GLFW_KEY_F, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Maxes out the brightness.");

	}

	@Override
	public void onDisable() {
		mc.gameSettings.gamma = 1;
	}

	@Override
	public void onEnable() {
		mc.gameSettings.gamma = 100000;
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
