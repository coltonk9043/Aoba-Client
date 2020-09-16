package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class Spider extends Module {

	public Spider() {
		this.setName("Spider");
		this.setBind(new KeyBinding("key.spider", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Allows players to climb up blocks.");
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
		if(player.collidedHorizontally) {
			player.getMotion().y = 0.2F;
			player.setOnGround(true);
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
