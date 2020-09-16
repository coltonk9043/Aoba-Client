package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class Step extends Module {

	private float stepHeight = 1f;
	
	public Step() {
		this.setName("Step");
		this.setBind(new KeyBinding("key.step", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Steps up blocks.");
	}

	@Override
	public void onDisable() {
		if(mc.world != null) {
			mc.player.stepHeight = .5f;
		}
	}

	@Override
	public void onEnable() {
		mc.player.stepHeight = stepHeight;
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
	
	public void setStepHeight(float height) {
		this.stepHeight = height;
	}
}
