package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;

public class Safewalk extends Module {

	public Safewalk() {
		this.setName("Safewalk");
		this.setBind(new KeyBinding("key.safewalk", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Permanently keeps player in sneaking mode.");
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
		double x = mc.player.getVelocity().x;
		double y = mc.player.getVelocity().y;
		double z = mc.player.getVelocity().z;
		if (mc.player.isOnGround()) {
			double increment;
			for (increment = 0.05D; x != 0.0D;) {
				if (x < increment && x >= -increment) {
					x = 0.0D;
				} else if (x > 0.0D) {
					x -= increment;
				} else {
					x += increment;
				}
			}
			for (; z != 0.0D;) {
				if (z < increment && z >= -increment) {
					z = 0.0D;
				} else if (z > 0.0D) {
					z -= increment;
				} else {
					z += increment;
				}
			}
			for (; x != 0.0D && z != 0.0D;) {
				if (x < increment && x >= -increment) {
					x = 0.0D;
				} else if (x > 0.0D) {
					x -= increment;
				} else {
					x += increment;
				}
				if (z < increment && z >= -increment) {
					z = 0.0D;
				} else if (z > 0.0D) {
					z -= increment;
				} else {
					z += increment;
				}
			}
		}
		mc.player.setVelocity(new Vec3d(x,y,z));
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

