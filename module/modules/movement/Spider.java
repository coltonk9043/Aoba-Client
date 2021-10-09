package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;

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
		if(player.horizontalCollision) {
			player.getVelocity().multiply(new Vec3d(1,0,1));
			player.getVelocity().add(new Vec3d(0,0.2,0));
			player.setOnGround(true);
		}
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
