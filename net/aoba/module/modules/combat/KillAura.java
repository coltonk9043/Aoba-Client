package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.BooleanSetting;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;

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
		if(mc.player.getAttackCooldownProgress(0) == 1) {
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
					|| mc.player.distanceTo(player) > this.radius.getValue()) {
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
					if (mc.player.distanceTo(player) <= mc.player
							.distanceTo(entityToAttack)) {
						entityToAttack = player;
						found = true;
					}
				}

			}
		}

		if (found && (mc.player.canSee(entityToAttack)
				&& entityToAttack instanceof LivingEntity)) {
			mc.player.attack(entityToAttack);
		}
	}

	public void attackEntities() {
		LivingEntity entityToAttack = null;

		for (Entity entity : mc.world.getEntities()) {
			if (entity == mc.player
					|| mc.player.distanceTo(entity) > this.radius.getValue() || !(entity instanceof LivingEntity)) {
				continue;
			}
			LivingEntity entityLiving = (LivingEntity) entity;
			if(entityToAttack == null) {
				entityToAttack = entityLiving;
			}else {
				if(this.priority == Priority.CLOSEST) {
					if(mc.player.distanceTo(entityToAttack) >= mc.player.distanceTo(entityLiving)) {
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
			mc.player.attack(entityToAttack);
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
