package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;

import net.aoba.misc.FakePlayerEntity;
import net.aoba.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;

public class POV extends Module {
	private FakePlayerEntity fakePlayer;
	private String povString = null;
	private Entity povEntity = null;
	
	private boolean fakePlayerSpawned = false;
	public POV() {
		this.setName("POV");
		this.setBind(new KeyBinding("key.pov", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see someone else's point-of-view.");
	}

	@Override
	public void onDisable() {
		MinecraftClient.getInstance().setCameraEntity(mc.player);
		if(fakePlayer != null) {
			fakePlayer.despawn();
			mc.world.removeEntity(-3, null);
		}
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
		povEntity = null;
		for(Entity entity : mc.world.getPlayers()) {
			if(entity.getName().getString().equals(povString)) {
				povEntity = entity;
			}
		}
		if(MinecraftClient.getInstance().getCameraEntity() == povEntity) {
			if(!fakePlayerSpawned) {
				fakePlayer = new FakePlayerEntity();
				fakePlayer.copyFrom(player);
				fakePlayer.headYaw = player.headYaw;
				mc.world.addEntity(-3, fakePlayer);
			}
			fakePlayer.copyFrom(player);
			fakePlayer.headYaw = player.headYaw;
		}else {
			if(fakePlayer != null) {
				fakePlayer.despawn();
				mc.world.removeEntity(-3, null);
			}
			
			if(povEntity == null) {
				MinecraftClient.getInstance().setCameraEntity(mc.player);
			}else {
				MinecraftClient.getInstance().setCameraEntity(povEntity);
			}
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
	
	public void setEntityPOV(String entity) {
		this.povString = entity;
	}

	public Entity getEntity() {
		return this.povEntity;
	}
	
	public PlayerEntity getEntityAsPlayer() {
		if(this.povEntity instanceof PlayerEntity) {
			return (PlayerEntity) this.povEntity;
		}else {
			return null;
		}
	}
}