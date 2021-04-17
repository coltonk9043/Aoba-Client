package aoba.main.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import aoba.main.module.Module;
import aoba.main.settings.BooleanSetting;
import aoba.main.settings.SliderSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;

public class KillAura extends Module {
	private enum Priority {
		LOWESTHP, CLOSEST
	}

	private Priority priority = Priority.LOWESTHP;
	private SliderSetting radius;
	private BooleanSetting targetAnimals;
	private BooleanSetting targetPlayers;
	
	public KillAura() {
		this.setName("KillAura");
		this.setBind(new KeyBinding("key.killaura", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Attacks anything within your personal space.");
		
		radius = new SliderSetting("Radius", "killaura_radius", 2f, 0.1f, 10f, 0.1f);
		targetAnimals = new BooleanSetting("Trgt Mobs", "killaura_target_mobs");
		targetPlayers = new BooleanSetting("Trgt Players", "killaura_target_players");
		this.addSetting(radius);
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
		if(mc.player.getCooledAttackStrength(0) == 1) {
			if(this.targetAnimals.getValue()) {
				attackEntities();
			}
			if(this.targetPlayers.getValue()) {
				attackPlayers();
			}
		}
	}

	public void attackPlayers() {
		PlayerEntity entityToAttack = null;
		boolean found = false;

		for (PlayerEntity player : mc.world.getPlayers()) {
			if (player == mc.player
					|| mc.player.getDistance(player) > this.radius.getValue()) {
				continue;
			}
			if (entityToAttack == null) {
				entityToAttack = player;
			} else {
				if (this.priority == Priority.LOWESTHP) {
					if (player.getHealth() <= entityToAttack.getHealth()) {
						entityToAttack = player;
						found = true;
					}
				} else if (this.priority == Priority.CLOSEST) {
					if (mc.player.getDistance(player) <= mc.player
							.getDistance(entityToAttack)) {
						entityToAttack = player;
						found = true;
					}
				}

			}
		}
		if (found && (mc.player.canEntityBeSeen(entityToAttack)
				&& entityToAttack instanceof LivingEntity)) {
			mc.playerController.attackEntity(mc.player, entityToAttack);
		}
	}

	public void attackEntities() {
		LivingEntity entityToAttack = null;

		for (Entity entity : mc.world.getAllEntities()) {
			if (entity == mc.player
					|| mc.player.getDistance(entity) > this.radius.getValue() || !(entity instanceof LivingEntity)) {
				continue;
			}
			LivingEntity entityLiving = (LivingEntity) entity;
			if(entityToAttack == null) {
				entityToAttack = entityLiving;
			}else {
				if(this.priority == Priority.CLOSEST) {
					if(mc.player.getDistance(entityToAttack) >= mc.player.getDistance(entityLiving)) {
						entityToAttack = entityLiving;
					}
				}else if(this.priority == Priority.LOWESTHP){
					if(entityToAttack.getHealth() <= entityLiving.getHealth()) {
						entityToAttack = entityLiving;
					}
				}
			}
		}
		if (entityToAttack != null && entityToAttack instanceof LivingEntity) {
			mc.playerController.attackEntity(mc.player, entityToAttack);
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
