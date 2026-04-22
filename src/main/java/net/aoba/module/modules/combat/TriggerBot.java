/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.player.InteractionUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TriggerBot extends Module implements TickListener {
	private final FloatSetting radius = FloatSetting.builder().id("triggerbot_radius").displayName("Radius")
			.description("Radius that TriggerBot will trigger.").defaultValue(3.00f).minValue(0.1f).maxValue(10f)
			.step(0.01f).build();

	private final BooleanSetting targetAnimals = BooleanSetting.builder().id("triggerbot_target_animals")
			.displayName("Target Animals").description("Target animals.").defaultValue(false).build();

	private final BooleanSetting targetMonsters = BooleanSetting.builder().id("triggerbot_target_monsters")
			.displayName("Target Monsters").description("Target Monsters.").defaultValue(true).build();

	private final BooleanSetting targetPlayers = BooleanSetting.builder().id("triggerbot_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

	private final BooleanSetting targetFriends = BooleanSetting.builder().id("triggerbot_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(false).build();

	private final FloatSetting attackDelay = FloatSetting.builder().id("triggerbot_attack_delay")
			.displayName("Attack Delay").description("Delay in milliseconds between attacks.").defaultValue(0f)
			.minValue(0f).maxValue(500f).step(10f).build();
	
	private final FloatSetting randomness = FloatSetting.builder().id("triggerbot_randomness").displayName("Randomness")
			.description("The randomness of the delay between when TriggerBot will hit a target.").defaultValue(0.0f)
			.minValue(0.0f).maxValue(60.0f).step(1f).build();

	private long lastAttackTime;

	public TriggerBot() {
		super("Triggerbot");

		setCategory(Category.of("Combat"));
		setDescription("Attacks anything you are looking at.");

		addSetting(attackDelay);
		addSetting(radius);
		addSetting(targetAnimals);
		addSetting(targetMonsters);
		addSetting(targetPlayers);
		addSetting(targetFriends);
		addSetting(randomness);

		lastAttackTime = 0L;
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
	public void onTick(Pre event) {
		if (System.currentTimeMillis() - lastAttackTime < attackDelay.getValue())
			return;

		// Randomly skip a tick using the randomness value.
		int randomnessValue = randomness.getValue().intValue();
		boolean state = randomnessValue == 0
				|| (Math.round(Math.random() * Math.round(randomness.max_value))) % randomnessValue == 0;
		
		if (MC.player.getAttackStrengthScale(1.0f) < 1|| !state)
			return;
		
		double reach = radius.getValue();
		Vec3 eyePos = MC.player.getEyePosition();
		Vec3 lookVector = MC.player.getViewVector(1.0F);
		Vec3 lookEndPos = eyePos.add(lookVector.x * reach, lookVector.y * reach, lookVector.z * reach);

		// TODO: We should likely move this to a helper
		// Check for a block is in the way of the player.
		BlockHitResult blockHit = MC.level.clip(new ClipContext(
				eyePos, lookEndPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, MC.player));
		if (blockHit.getType() != HitResult.Type.MISS) {
			lookEndPos = blockHit.getLocation();
		}
		
		// Perform our own hit-cast using radius
		double rayDistSqr = eyePos.distanceToSqr(lookEndPos);
		AABB hitcastSearchBox = MC.player.getBoundingBox().expandTowards(lookVector.scale(reach)).inflate(1.0, 1.0, 1.0);
		EntityHitResult entityResult = ProjectileUtil.getEntityHitResult(
				MC.player, eyePos, lookEndPos, hitcastSearchBox,
				e -> !e.isSpectator() && e.isPickable() && e != MC.player,
				rayDistSqr);

		if (entityResult != null) {
			Entity ent = entityResult.getEntity();
			if (!(ent instanceof LivingEntity) || !ent.isAlive())
				return;

			// Filter out entities which are NOT allowed to be hit.
			if (ent instanceof Animal && !targetAnimals.getValue())
				return;
			if (ent instanceof Player && !targetPlayers.getValue() || (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(ent.getUUID())))
				return;
			if (ent instanceof Enemy && !targetMonsters.getValue())
				return;

			// Get the distance from the edges of the hitbox.
			AABB box = ent.getBoundingBox();
			double cx = Math.max(box.minX, Math.min(eyePos.x, box.maxX));
			double cy = Math.max(box.minY, Math.min(eyePos.y, box.maxY));
			double cz = Math.max(box.minZ, Math.min(eyePos.z, box.maxZ));
			double dx = cx - eyePos.x, dy = cy - eyePos.y, dz = cz - eyePos.z;
			double distSqr = dx * dx + dy * dy + dz * dz;

			if (distSqr > radius.getValueSqr())
				return;

			InteractionUtils.attack(ent);
			lastAttackTime = System.currentTimeMillis();
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
