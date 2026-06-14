/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import java.util.ArrayList;
import java.util.Set;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.SubtickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.SubtickListener;
import net.aoba.gui.colors.Color;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.EntityGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.rendering.shaders.Shader;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EntitiesSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.ShaderSetting;
import net.aoba.utils.entity.BodyPart;
import net.aoba.utils.entity.EntityUtils;
import net.aoba.utils.entity.TargetPriority;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Aimbot extends Module implements SubtickListener, Render3DListener {

	private LivingEntity currentTarget = null;

	private final EnumSetting<TargetPriority> targetPriority = EnumSetting.<TargetPriority>builder()
			.id("aimbot_target_priority").displayName("Target Priority")
			.description("The priority used to pick which target to aim towards.").defaultValue(TargetPriority.CLOSEST)
			.build();

	private final EnumSetting<BodyPart> bodyPart = EnumSetting.<BodyPart>builder().id("aimbot_body_part")
			.displayName("Body Part").description("The part of the target's body to aim at.")
			.defaultValue(BodyPart.HEAD).build();

	private final EntitiesSetting targetEntities = EntitiesSetting.builder().id("aimbot_target_entities")
			.displayName("Target Entities")
			.description("Entity types that Aimbot will target.")
			.defaultValue(Set.of(EntityType.PLAYER)).build();

	private final BooleanSetting targetFriends = BooleanSetting.builder().id("aimbot_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(false).build();

	private final BooleanSetting ignoreDead = BooleanSetting.builder().id("aimbot_ignore_dead")
			.displayName("Ignore Dead").description("Skip entities that are dead or dying.").defaultValue(true).build();

	private final BooleanSetting ignoreInvisible = BooleanSetting.builder().id("aimbot_ignore_invisible")
			.displayName("Ignore Invisible").description("Skip entities that are invisible.").defaultValue(true)
			.build();

	private final BooleanSetting ignoreSleeping = BooleanSetting.builder().id("aimbot_ignore_sleeping")
			.displayName("Ignore Sleeping").description("Skip players that are sleeping.").defaultValue(true).build();

	private final BooleanSetting ignoreNPCs = BooleanSetting.builder().id("aimbot_ignore_npcs")
			.displayName("Ignore NPCs").description("Attempts to ignore NPCs based on the entity UUID.")
			.defaultValue(true).build();

	private final BooleanSetting useRaycast = BooleanSetting.builder().id("aimbot_use_raycast").displayName("Use Raycast")
			.description("Skips targets that are not in the line of sight of the player.").defaultValue(true).build();

	private final FloatSetting radius = FloatSetting.builder().id("aimbot_radius").displayName("Radius")
			.description("Radius that the aimbot will lock onto a target.").defaultValue(64.0f).minValue(1.0f)
			.maxValue(256.0f).step(1.0f).build();

	private final FloatSetting fov = FloatSetting.builder().id("aimbot_fov").displayName("FOV")
			.description("Angular cone in front of the player that targets must fall within.").defaultValue(90.0f)
			.minValue(1.0f).maxValue(360.0f).step(1.0f).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("aimbot_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private final FloatSetting maxRotation = FloatSetting.builder().id("aimbot_max_rotation")
			.displayName("Max Rotation").description("The max speed that Aimbot will rotate").defaultValue(10.0f)
			.minValue(1.0f).maxValue(360.0f).build();

	private final FloatSetting yawRandomness = FloatSetting.builder().id("aimbot_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final FloatSetting pitchRandomness = FloatSetting.builder().id("aimbot_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final BooleanSetting fakeRotation = BooleanSetting.builder().id("aimbot_fake_rotation")
			.displayName("Fake Rotation")
			.description("Spoofs the client's rotation so that the player appears rotated on the server")
			.defaultValue(false).build();

	private final BooleanSetting moveFix = BooleanSetting.builder().id("aimbot_move_fix").displayName("Move Fix")
			.description("Corrects movement to match spoofed rotation by using the server yaw for velocity.")
			.defaultValue(false).build();

	private final ShaderSetting targetColor = ShaderSetting.builder().id("aimbot_target_color")
			.displayName("Target Color").description("Color of the box drawn at the target position.")
			.defaultValue(Shader.solid(new Color(255, 0, 0))).build();

	public Aimbot() {
		super("Aimbot");
		setCategory(Category.of("Combat"));
		setDescription("Locks your crosshair towards a desired player or entity.");

		addSetting(targetPriority);
		addSetting(bodyPart);
		addSetting(targetEntities);
		addSetting(targetFriends);
		addSetting(ignoreDead);
		addSetting(ignoreInvisible);
		addSetting(ignoreSleeping);
		addSetting(ignoreNPCs);
		addSetting(useRaycast);
		addSetting(radius);
		addSetting(fov);
		addSetting(rotationMode);
		addSetting(maxRotation);
		addSetting(yawRandomness);
		addSetting(pitchRandomness);
		addSetting(fakeRotation);
		addSetting(moveFix);
		addSetting(targetColor);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(SubtickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().rotationManager.setGoal(null);
		currentTarget = null;
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(SubtickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSubtick(SubtickEvent event) {
		float partialTick = MC.getDeltaTracker().getGameTimeDeltaPartialTick(true);
		Vec3 playerPos = MC.player.getPosition(partialTick);

		float radiusSqr = radius.getValueSqr();
		ArrayList<LivingEntity> hitList = new ArrayList<LivingEntity>();

		Set<EntityType<?>> allowed = targetEntities.getValue();
		if (!allowed.isEmpty()) {
			for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
				if (!(entity instanceof LivingEntity living))
					continue;

				if (entity == MC.player)
					continue;

				if (!allowed.contains(entity.getType()))
					continue;

				if (entity.getPosition(partialTick).distanceToSqr(playerPos) >= radiusSqr)
					continue;

				if (!shouldTarget(living, partialTick))
					continue;

				hitList.add(living);
			}
		}

		LivingEntity entityFound = null;
		for (LivingEntity entity : hitList) {
			if (entityFound == null) {
				entityFound = entity;
			} else if (targetPriority.getValue() == TargetPriority.LOWEST_HEALTH) {
				if (entity.getHealth() <= entityFound.getHealth()) {
					entityFound = entity;
				}
			} else if (targetPriority.getValue() == TargetPriority.MOST_HEALTH) {
				if (entity.getHealth() >= entityFound.getHealth()) {
					entityFound = entity;
				}
			} else if (playerPos.distanceToSqr(entity.getPosition(partialTick)) <= playerPos
					.distanceToSqr(entityFound.getPosition(partialTick))) {
				entityFound = entity;
			}
		}

		if (entityFound != null) {
			EntityGoal rotation = EntityGoal.builder().goal(entityFound).mode(rotationMode.getValue())
					.maxRotation(maxRotation.getValue()).pitchRandomness(pitchRandomness.getValue())
					.yawRandomness(yawRandomness.getValue()).fakeRotation(fakeRotation.getValue())
					.moveFix(moveFix.getValue()).bodyPart(bodyPart.getValue()).build();
			Aoba.getInstance().rotationManager.setGoal(rotation);
			currentTarget = entityFound;
		} else {
			Aoba.getInstance().rotationManager.setGoal(null);
			currentTarget = null;
		}
	}

	@Override
	public void onRender(Render3DEvent event) {
		if (currentTarget == null || !currentTarget.isAlive())
			return;

		float tickDelta = event.getRenderer().getDeltaTracker().getGameTimeDeltaPartialTick(true);
		Vec3 targetPos = EntityUtils.getBodyPartPosition(currentTarget, bodyPart.getValue(), tickDelta);
		AABB targetBox = new AABB(targetPos.x - 0.1, targetPos.y - 0.1, targetPos.z - 0.1, targetPos.x + 0.1,
				targetPos.y + 0.1, targetPos.z + 0.1);
		event.getRenderer().drawBox(targetBox, targetColor.getValue(), 1.0f);
	}

	private boolean shouldTarget(LivingEntity entity, float partialTick) {
		if (!EntityUtils.isInFOV(entity, bodyPart.getValue(), fov.getValue(), partialTick))
			return false;

		if (ignoreDead.getValue() && !entity.isAlive())
			return false;

		if (ignoreInvisible.getValue() && entity.isInvisible())
			return false;

		if (ignoreSleeping.getValue() && entity.isSleeping())
			return false;

		// Attempt to skips NPC and friends.
		if (entity instanceof Player player) {
			if (!targetFriends.getValue() && EntityUtils.isFriend(player))
				return false;
			if (ignoreNPCs.getValue() && EntityUtils.isNPC(player))
				return false;
		}

		// Perform raycast to skip players not visible.
		if (useRaycast.getValue()) {
			Vec3 eyePos = MC.player.getEyePosition(partialTick);
			Vec3 targetEyePos = entity.getEyePosition(partialTick);
			BlockHitResult hit = MC.level.clip(new ClipContext(eyePos, targetEyePos, ClipContext.Block.COLLIDER,
					ClipContext.Fluid.NONE, MC.player));
			if (hit.getType() != HitResult.Type.MISS)
				return false;
		}
		return true;
	}
}