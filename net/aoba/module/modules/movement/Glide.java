package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

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
		if(player.getVelocity().y < 0 && (!player.isOnGround() || !player.isInLava() || !player.isSubmergedInWater() || !player.isHoldingOntoLadder())) {
			player.setVelocity(player.getVelocity().x, Math.max(player.getVelocity().y, -this.fallSpeed), player.getVelocity().z);
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack) {

	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
}
