package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;

public class Noclip extends Module {
	private float flySpeed = 5;
	
	public Noclip() {
		this.setName("Noclip");
		this.setBind(new KeyBinding("key.noclip", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to clip through blocks (Only work clientside).");
	}

	public void setSpeed(float speed) {
		this.flySpeed = speed;
	}
	
	@Override
	public void onDisable() {
		MC.player.noClip = false;
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		ClientPlayerEntity player = MC.player;
		player.noClip = true;
		if (MC.options.sprintKey.isPressed()) {
			this.flySpeed *= 1.5;
		}
		player.setVelocity(new Vec3d(0,0,0));
		//player.setAIMoveSpeed(flySpeed * 0.2f);
		//player.jumpMovementFactor = flySpeed * 0.2f;

		Vec3d vec = new Vec3d(0,0,0);
		if (MC.options.jumpKey.isPressed()) {
			vec = new Vec3d(0,flySpeed * 0.2f,0);
		}
		if (MC.options.sneakKey.isPressed()) {
			vec = new Vec3d(0,-flySpeed * 0.2f,0);
		}
		if (MC.options.sprintKey.isPressed()) {
			this.flySpeed /= 1.5;
		}
		player.setVelocity(vec);
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
		
		
	}
}
