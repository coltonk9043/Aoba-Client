package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;

public class Fly extends Module {

	private SliderSetting flySpeed;
	
	public Fly() {
		this.setName("Fly");
		this.setBind(new KeyBinding("key.fly", GLFW.GLFW_KEY_V, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to fly.");
		
		flySpeed = new SliderSetting("Speed", "fly_speed", 2f, 0.1f, 15f, 0.5f);
		this.addSetting(flySpeed);
	}

	public void setSpeed(float speed) {
		this.flySpeed.setValue(speed);
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
		float speed = this.flySpeed.getValueFloat();
		if(mc.player.isRiding()) {
			Entity riding = mc.player.getRootVehicle();
			Vec3d velocity = riding.getVelocity();
			double motionY = mc.options.keyJump.isPressed() ? 0.3 : 0;
			riding.setVelocity(velocity.x, motionY, velocity.z);
		}else {
			if (mc.options.keySprint.isPressed()) {
				speed *= 1.5;
			}
			player.setVelocity(new Vec3d(0, 0, 0));
			//player.setAIMoveSpeed(speed * 0.2f);
			//player.jumpMovementFactor = speed * 0.2f;

			Vec3d vec = new Vec3d(0, 0, 0);
			if (mc.options.keyJump.isPressed()) {
				vec = new Vec3d(0, speed * 0.2f, 0);
			}
			if (mc.options.keySneak.isPressed()) {
				vec = new Vec3d(0, -speed * 0.2f, 0);
			}
			player.setVelocity(vec);
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
