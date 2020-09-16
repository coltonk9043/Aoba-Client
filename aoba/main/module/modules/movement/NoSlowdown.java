package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;

public class NoSlowdown extends Module {

	public NoSlowdown() {
		this.setName("NoSlowdown");
		this.setBind(new KeyBinding("key.noslowdown", GLFW.GLFW_KEY_Z, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Prevents the player from being slowed down by blocks.");
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
		mc.player.setMotionMultiplier(null, Vector3d.ZERO);
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
