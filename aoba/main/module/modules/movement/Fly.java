package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import aoba.main.settings.SliderSetting;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;;

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
			Entity riding = mc.player.getRidingEntity();
			Vector3d velocity = riding.getMotion();
			double motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 0.3 : 0;
			riding.setVelocity(velocity.x, motionY, velocity.z);
		}else {
			if (mc.gameSettings.keyBindSprint.isKeyDown()) {
				speed *= 1.5;
			}
			player.setMotion(new Vector3d(0, 0, 0));
			player.setAIMoveSpeed(speed * 0.2f);
			player.jumpMovementFactor = speed * 0.2f;

			Vector3d vec = new Vector3d(0, 0, 0);
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				vec.y = speed * 0.2f;
			}
			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				vec.y = -speed * 0.2f;
			}
			player.setMotion(vec);
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
