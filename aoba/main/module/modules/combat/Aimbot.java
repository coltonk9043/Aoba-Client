package aoba.main.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import aoba.main.settings.BooleanSetting;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;

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
						if (mc.world.getPlayers().get(x).getDistance(
								mc.player) < mc.world.getPlayers().get(y)
										.getDistance(mc.player)) {
							temp = mc.world.getPlayers().get(x);
						}
					}
				}
			}
		}
		if(this.targetAnimals.getValue()) {
			LivingEntity tempEntity = null;
			for(Entity entity : mc.world.getAllEntities()) {
				if(!(entity instanceof LivingEntity)) continue;
				if(entity instanceof ClientPlayerEntity) continue;
				if(tempEntity == null) {
					tempEntity = (LivingEntity) entity; 
				}else {
					if(entity.getDistance(mc.player) < tempEntity.getDistance(mc.player)) {
						tempEntity = (LivingEntity) entity;
					}
				}
			}
			temp = tempEntity;
		}
	}

	@Override
	public void onRender() {
		if (temp != null) {
			mc.player.lookAt(temp);
		}
	}

	@Override
	public void onSendPacket(IPacket<?> packet) {

	}

	@Override
	public void onReceivePacket(IPacket<?> packet) {

	}
}