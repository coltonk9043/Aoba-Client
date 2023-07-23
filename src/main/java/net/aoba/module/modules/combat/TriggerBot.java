package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.aoba.settings.BooleanSetting;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TriggerBot extends Module {
	private SliderSetting radius;
	private BooleanSetting targetAnimals;
	private BooleanSetting targetMonsters;
	private BooleanSetting targetPlayers;

	
	public TriggerBot() {
		this.setName("Triggerbot");
		this.setBind(new KeyBinding("key.triggerbot", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Attacks anything you are looking at.");

		radius = new SliderSetting("Radius", "triggerbot_radius", 5f, 0.1f, 10f, 0.1f);
		targetAnimals = new BooleanSetting("Trgt Animals", "triggerbot_target_animals");
		targetMonsters = new BooleanSetting("Trgt Monsters", "triggerbot_target_monsters");
		targetPlayers = new BooleanSetting("Trgt Players", "triggerbot_target_players");
		this.addSetting(radius);
		this.addSetting(targetAnimals);
		this.addSetting(targetMonsters);
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
		// Get the current target that the player is looking at.
		HitResult ray = MC.crosshairTarget;
		
		// If the target is an Entity, attack it.
		if(ray != null && ray.getType()==HitResult.Type.ENTITY) {
			EntityHitResult entityResult = (EntityHitResult) ray;
			Entity ent = entityResult.getEntity();
			if(ent instanceof AnimalEntity && !this.targetAnimals.getValue()) return;
			if(ent instanceof PlayerEntity && !this.targetPlayers.getValue()) return;
			if(ent instanceof Monster && !this.targetMonsters.getValue()) return;
			
			if(MC.player.getAttackCooldownProgress(0) == 1) {
				MC.interactionManager.attackEntity(MC.player, entityResult.getEntity());
				MC.player.swingHand(Hand.MAIN_HAND);
			}
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {

	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
}
