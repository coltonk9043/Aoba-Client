/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import java.util.Set;

import net.aoba.Aoba;
import net.aoba.event.events.SubtickEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.SubtickListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;

import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EntitiesSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.RangeSetting;
import net.aoba.utils.types.Range;
import net.aoba.utils.entity.EntityUtils;
import net.aoba.utils.player.InteractionUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TriggerBot extends Module implements SubtickListener, TickListener {
	private final FloatSetting radius = FloatSetting.builder().id("triggerbot_radius").displayName("Radius")
			.description("Radius that TriggerBot will trigger.").defaultValue(3.00f).minValue(0.1f).maxValue(10f)
			.step(0.01f).build();

	private final EntitiesSetting targetEntities = EntitiesSetting.builder().id("triggerbot_target_entities")
			.displayName("Target Entities")
			.description("Entity types that TriggerBot will target.")
			.defaultValue(Set.of(EntityTypes.PLAYER)).build();

	private final BooleanSetting targetFriends = BooleanSetting.builder().id("triggerbot_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(false).build();

	private final BooleanSetting ignoreDead = BooleanSetting.builder().id("triggerbot_ignore_dead")
			.displayName("Ignore Dead").description("Skip entities that are dead or dying.").defaultValue(true).build();

	private final BooleanSetting ignoreInvisible = BooleanSetting.builder().id("triggerbot_ignore_invisible")
			.displayName("Ignore Invisible").description("Skip entities that are invisible.").defaultValue(true)
			.build();

	private final BooleanSetting ignoreSleeping = BooleanSetting.builder().id("triggerbot_ignore_sleeping")
			.displayName("Ignore Sleeping").description("Skip players that are sleeping.").defaultValue(true).build();

	private final BooleanSetting ignoreNPCs = BooleanSetting.builder().id("triggerbot_ignore_npcs")
			.displayName("Ignore NPCs").description("Attempts to ignore NPCs based on the entity UUID.")
			.defaultValue(true).build();

	private final RangeSetting attackDelay = RangeSetting.builder().id("triggerbot_attack_delay")
			.displayName("Attack Delay").description("Random delay in milliseconds between attacks (min, max).")
			.defaultValue(new Range(50f, 150f)).minValue(0f).maxValue(1000f).step(10f).build();

	private final BooleanSetting triggerOnClick = BooleanSetting.builder().id("triggerbot_trigger_on_click")
			.displayName("Trigger On Click").description("Only attack while the attack key is held down.")
			.defaultValue(false).build();

	private float timeSinceAttackMs;
	private float nextAttackDelayMs;
	private Entity pendingAttackTarget;

	public TriggerBot() {
		super("Triggerbot");

		setCategory(Category.of("Combat"));
		setDescription("Attacks anything you are looking at.");

		addSetting(attackDelay);
		addSetting(triggerOnClick);
		addSetting(radius);
		addSetting(targetEntities);
		addSetting(targetFriends);
		addSetting(ignoreDead);
		addSetting(ignoreInvisible);
		addSetting(ignoreSleeping);
		addSetting(ignoreNPCs);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(SubtickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		pendingAttackTarget = null;
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(SubtickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
		timeSinceAttackMs = 0f;
		nextAttackDelayMs = attackDelay.randomValue();
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSubtick(SubtickEvent event) {
		timeSinceAttackMs += event.getDelta();

		if (pendingAttackTarget != null)
			return;
		if (timeSinceAttackMs < nextAttackDelayMs)
			return;
		if (MC.player.getAttackStrengthScale(1.0f) < 1)
			return;

		double reach = radius.getValue();
		Vec3 eyePos = MC.player.getEyePosition();
		Vec3 lookVector = MC.player.getViewVector(1.0F);
		Vec3 lookEndPos = eyePos.add(lookVector.x * reach, lookVector.y * reach, lookVector.z * reach);

		// TODO: We should likely move this to a helper
		// Check for a block is in the way of the player.
		BlockHitResult blockHit = MC.level.clip(
				new ClipContext(eyePos, lookEndPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, MC.player));
		if (blockHit.getType() != HitResult.Type.MISS) {
			lookEndPos = blockHit.getLocation();
		}

		// Perform our own hit-cast using radius
		double rayDistSqr = eyePos.distanceToSqr(lookEndPos);
		AABB hitcastSearchBox = MC.player.getBoundingBox().expandTowards(lookVector.scale(reach)).inflate(1.0, 1.0,
				1.0);
		EntityHitResult entityResult = ProjectileUtil.getEntityHitResult(MC.player, eyePos, lookEndPos,
				hitcastSearchBox, e -> !e.isSpectator() && e.isPickable() && e != MC.player, rayDistSqr);

		if (entityResult != null) {
			Entity ent = entityResult.getEntity();
			if (!(ent instanceof LivingEntity living))
				return;

			// Filter out entities which are NOT allowed to be hit.
			if (!targetEntities.getValue().contains(ent.getType()))
				return;
			if (ent instanceof Player player) {
				if (!targetFriends.getValue() && EntityUtils.isFriend(player))
					return;
				if (ignoreNPCs.getValue() && EntityUtils.isNPC(player))
					return;
			}

			if (ignoreDead.getValue() && !living.isAlive())
				return;
			if (ignoreInvisible.getValue() && living.isInvisible())
				return;
			if (ignoreSleeping.getValue() && living.isSleeping())
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

			pendingAttackTarget = ent;
		}
	}

	@Override
	public void onTick(TickEvent.Pre event) {
		if (triggerOnClick.getValue() && !MC.options.keyAttack.isDown()) {
			pendingAttackTarget = null;
			return;
		}

		Entity target = pendingAttackTarget;
		pendingAttackTarget = null;
		if (target == null)
			return;
		if (target.isRemoved() || !target.isAlive())
			return;

		InteractionUtils.attack(target);
		timeSinceAttackMs = 0f;
		nextAttackDelayMs = attackDelay.randomValue();
	}

	@Override
	public void onTick(TickEvent.Post event) {
	}
}
