package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;

public class Glide extends Module {
	private float fallSpeed = .25f;
	public Glide() {
		this.setName("Glide");
		this.setBind(new KeyBinding("key.glide", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to glide down.");
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
		if(player.getMotion().y < 0 && (!player.isOnGround() || !player.isInLava() || !player.isInWater() || !player.isOnLadder())) {
			player.setVelocity(player.getMotion().x, Math.max(player.getMotion().y, -this.fallSpeed), player.getMotion().z);
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
