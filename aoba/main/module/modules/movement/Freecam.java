package aoba.main.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import aoba.main.misc.FakePlayerEntity;
import aoba.main.module.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;

public class Freecam extends Module {
	private FakePlayerEntity fakePlayer;
	private float flySpeed = 5;
	
	public Freecam() {
		this.setName("Freecam");
		this.setBind(new KeyBinding("key.freecam", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to clip through blocks (Only work clientside).");
	}

	@Override
	public void onDisable() {
		ClientPlayerEntity player = mc.player;
		mc.player.noClip = false;
		player.setVelocity(0, 0, 0);
		player.copyDataFromOld(fakePlayer);
		fakePlayer.despawn();
		mc.world.removeEntityFromWorld(-3);
	}

	@Override
	public void onEnable() {
		ClientPlayerEntity player = mc.player;
		fakePlayer = new FakePlayerEntity();
		fakePlayer.copyDataFromOld(player);
		fakePlayer.rotationYawHead = player.rotationYawHead;
		mc.world.addEntity(-3, fakePlayer);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		ClientPlayerEntity player = mc.player;
		player.noClip = true;
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
	
	public FakePlayerEntity getFakePlayer() {
		return this.fakePlayer;
	}
}
