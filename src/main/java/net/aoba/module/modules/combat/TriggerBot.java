package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;

import net.aoba.Aoba;
import net.aoba.core.settings.types.BooleanSetting;
import net.aoba.core.settings.types.FloatSetting;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TriggerBot extends Module implements TickListener {
	private FloatSetting radius;
	private BooleanSetting targetAnimals;
	private BooleanSetting targetMonsters;
	private BooleanSetting targetPlayers;

	public TriggerBot() {
		this.setName("Triggerbot");
		this.setBind(new KeyBinding("key.triggerbot", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Attacks anything you are looking at.");

		radius = new FloatSetting("triggerbot_radius", "Radius", 5f, 0.1f, 10f, 0.1f);
		targetAnimals = new BooleanSetting("triggerbot_target_animals", "Target animals.", false, null);
		targetMonsters = new BooleanSetting("triggerbot_target_monsters", "Target monsters.", true, null);
		targetPlayers = new BooleanSetting("triggerbot_target_players", "Target players.", true, null);
		this.addSetting(radius);
		this.addSetting(targetAnimals);
		this.addSetting(targetMonsters);
		this.addSetting(targetPlayers);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void OnUpdate(TickEvent event) {
		// Get the current target that the player is looking at.
		HitResult ray = MC.crosshairTarget;

		// If the target is an Entity, attack it.
		if (ray != null && ray.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityResult = (EntityHitResult) ray;
			Entity ent = entityResult.getEntity();
			if (ent instanceof AnimalEntity && !this.targetAnimals.getValue())
				return;
			if (ent instanceof PlayerEntity && !this.targetPlayers.getValue())
				return;
			if (ent instanceof Monster && !this.targetMonsters.getValue())
				return;

			if (MC.player.getAttackCooldownProgress(0) == 1) {
				MC.interactionManager.attackEntity(MC.player, entityResult.getEntity());
				MC.player.swingHand(Hand.MAIN_HAND);
			}
		}
	}
}
