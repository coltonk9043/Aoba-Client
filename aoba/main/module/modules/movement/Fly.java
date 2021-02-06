package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;;

public class Fly extends Module {

	private float flySpeed = 2;

	public Fly() {
		this.setName("Fly");
		this.setBind(new KeyBinding("key.fly", GLFW.GLFW_KEY_V, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to fly.");
	}

	public void setSpeed(float speed) {
		this.flySpeed = speed;
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
		
		if (mc.gameSettings.keyBindSprint.isKeyDown()) {
			this.flySpeed *= 1.5;
		}
		player.setMotion(new Vector3d(0,0,0));
		player.setAIMoveSpeed(flySpeed * 0.2f);
		player.jumpMovementFactor = flySpeed * 0.2f;

		Vector3d vec = new Vector3d(0,0,0);
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			vec.y = flySpeed * 0.2f;
		}
		if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			vec.y = -flySpeed * 0.2f;
		}
		if (!player.isOnGround())
			player.connection.sendPacket(new CPlayerPacket(true));
		if (mc.gameSettings.keyBindSprint.isKeyDown()) {
			this.flySpeed /= 1.5;
		}
		player.setMotion(vec);
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
