package aoba.main.module.modules.render;

import org.lwjgl.glfw.GLFW;

import aoba.main.misc.FakePlayerEntity;
import aoba.main.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;

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
		Minecraft.getInstance().setRenderViewEntity(mc.player);
		if(fakePlayer != null) {
			fakePlayer.despawn();
			mc.world.removeEntityFromWorld(-3);
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
		if(Minecraft.getInstance().getRenderViewEntity() == povEntity) {
			if(!fakePlayerSpawned) {
				fakePlayer = new FakePlayerEntity();
				fakePlayer.copyDataFromOld(player);
				fakePlayer.rotationYawHead = player.rotationYawHead;
				mc.world.addEntity(-3, fakePlayer);
			}
			fakePlayer.copyDataFromOld(player);
			fakePlayer.rotationYawHead = player.rotationYawHead;
		}else {
			if(fakePlayer != null) {
				fakePlayer.despawn();
				mc.world.removeEntityFromWorld(-3);
			}
			
			if(povEntity == null) {
				Minecraft.getInstance().setRenderViewEntity(mc.player);
			}else {
				Minecraft.getInstance().setRenderViewEntity(povEntity);
			}
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
	
	public void setEntityPOV(String entity) {
		this.povString = entity;
	}

}