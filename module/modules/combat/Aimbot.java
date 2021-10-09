package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.BooleanSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.Packet;

public class Aimbot extends Module {
	
	private LivingEntity temp = null;
	
	private BooleanSetting targetAnimals;
	private BooleanSetting targetPlayers;
	
	public Aimbot() {
		this.setName("Aimbot");
		this.setBind(new KeyBinding("key.aimbot", GLFW.GLFW_KEY_K, "key.categories.aoba"));
		
		this.setCategory(Category.Combat);
		this.setDescription("Locks your crosshair towards a desire player or entity.");
		targetAnimals = new BooleanSetting("Trgt Mobs", "aimbot_target_mobs");
		targetPlayers = new BooleanSetting("Trgt Players", "aimbot_target_players");
		this.addSetting(targetAnimals);
		this.addSetting(targetPlayers);
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
	
		if(this.targetPlayers.getValue()) {
			if (mc.world.getPlayers().size() == 2) {
				temp = mc.world.getPlayers().get(1);
			} else if (mc.world.getPlayers().size() > 2) {
				for (int x = 0; x < mc.world.getPlayers().size(); x++) {
					for (int y = 1; y < mc.world.getPlayers().size(); y++) {
						if (mc.world.getPlayers().get(x).distanceTo(
								mc.player) < mc.world.getPlayers().get(y)
										.distanceTo(mc.player)) {
							temp = mc.world.getPlayers().get(x);
						}
					}
				}
			}
		}
		if(this.targetAnimals.getValue()) {
			LivingEntity tempEntity = null;
			for(Entity entity : mc.world.getEntities()) {
				if(!(entity instanceof LivingEntity)) continue;
				if(entity instanceof ClientPlayerEntity) continue;
				if(tempEntity == null) {
					tempEntity = (LivingEntity) entity; 
				}else {
					if(entity.distanceTo(mc.player) < tempEntity.distanceTo(mc.player)) {
						tempEntity = (LivingEntity) entity;
					}
				}
			}
			temp = tempEntity;
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		if (temp != null) {
			mc.player.lookAt(null, temp.getEyePos());
		}
	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
}