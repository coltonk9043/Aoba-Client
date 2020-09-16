package aoba.main.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;

public class Aimbot extends Module {
	public enum Mode {
		PLAYER, ENTITY, BOTH
	}
	
	private Mode mode = Mode.ENTITY;
	private LivingEntity temp = null;
	
	public Aimbot() {
		this.setName("Aimbot");
		this.setBind(new KeyBinding("key.aimbot", GLFW.GLFW_KEY_K, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Locks your crosshair towards a desire player or entity.");
	}

	public void changeMode(Mode mode) {
		this.mode = mode;
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
	
		switch (this.mode) {
		case PLAYER:
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
			break;
		case ENTITY:
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
			break;
		case BOTH:
			break;
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