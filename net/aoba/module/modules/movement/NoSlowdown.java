package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;

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
		//mc.player.setMotionMultiplier(null, Vec3d.ZERO);
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
