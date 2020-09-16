package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class Jesus extends Module {
	public Jesus() {
		this.setName("Jesus");
		this.setBind(new KeyBinding("key.jesus", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to walk on water.");
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
		ClientPlayerEntity player = mc.player;
		if (mc.player.isInWater()) {
			player.setVelocity(player.getMotion().x, 0.11, player.getMotion().z);
		}	
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
