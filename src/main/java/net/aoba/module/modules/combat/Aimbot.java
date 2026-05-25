/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import java.util.ArrayList;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Color;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.EntityGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.rendering.shaders.Shader;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.ShaderSetting;
import net.aoba.utils.entity.BodyPart;
import net.aoba.utils.entity.EntityUtils;
import net.aoba.utils.entity.TargetPriority;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Aimbot extends Module implements TickListener, Render3DListener {

	private LivingEntity currentTarget = null;
	
	private final EnumSetting<TargetPriority> targetPriority = EnumSetting.<TargetPriority>builder()
			.id("aimbot_target_priority").displayName("Target Priority")
			.description("The priority used to pick which target to aim towards.")
			.defaultValue(TargetPriority.CLOSEST).build();

	private final EnumSetting<BodyPart> bodyPart = EnumSetting.<BodyPart>builder().id("aimbot_body_part")
			.displayName("Body Part").description("The part of the target's body to aim at.")
			.defaultValue(BodyPart.HEAD).build();

	private final BooleanSetting targetAnimals = BooleanSetting.builder().id("aimbot_target_animals")
			.displayName("Target Animals").description("Target passive animals.").defaultValue(false).build();

	private final BooleanSetting targetMonsters = BooleanSetting.builder().id("aimbot_target_monsters")
			.displayName("Target Monsters").description("Target hostile mobs.").defaultValue(false).build();

	private final BooleanSetting targetPlayers = BooleanSetting.builder().id("aimbot_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

	private final BooleanSetting targetFriends = BooleanSetting.builder().id("aimbot_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(true).build();

	private final BooleanSetting ignoreDead = BooleanSetting.builder().id("aimbot_ignore_dead")
			.displayName("Ignore Dead").description("Skip entities that are dead or dying.").defaultValue(true)
			.build();

	private final BooleanSetting ignoreInvisible = BooleanSetting.builder().id("aimbot_ignore_invisible")
			.displayName("Ignore Invisible").description("Skip entities that are invisible.").defaultValue(true)
			.build();

	private final BooleanSetting ignoreSleeping = BooleanSetting.builder().id("aimbot_ignore_sleeping")
			.displayName("Ignore Sleeping").description("Skip players that are sleeping.").defaultValue(true)
			.build();

	private final BooleanSetting ignoreNPCs = BooleanSetting.builder().id("aimbot_ignore_npcs")
			.displayName("Ignore NPCs")
			.description("Attempts to ignore NPCs based on the entity UUID.").defaultValue(false).build();

	private final BooleanSetting legit = BooleanSetting.builder().id("aimbot_legit").displayName("Legit")
			.description("Skips targets that are not in the line of sight of the player.")
			.defaultValue(false).build();

	private final FloatSetting radius = FloatSetting.builder().id("aimbot_radius").displayName("Radius")
			.description("Radius that the aimbot will lock onto a target.").defaultValue(64.0f).minValue(1.0f)
			.maxValue(256.0f).step(1.0f).build();

	private final FloatSetting fov = FloatSetting.builder().id("aimbot_fov").displayName("FOV")
			.description("Angular cone in front of the player that targets must fall within.")
			.defaultValue(360.0f).minValue(1.0f).maxValue(360.0f).step(1.0f).build();

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

	private final BooleanSetting moveFix = BooleanSetting.builder().id("aimbot_move_fix")
			.displayName("Move Fix")
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
		addSetting(targetAnimals);
		addSetting(targetMonsters);
		addSetting(targetPlayers);
		addSetting(targetFriends);
		addSetting(ignoreDead);
		addSetting(ignoreInvisible);
		addSetting(ignoreSleeping);
		addSetting(ignoreNPCs);
		addSetting(legit);
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
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().rotationManager.setGoal(null);
		currentTarget = null;
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
		float radiusSqr = radius.getValueSqr();
		ArrayList<LivingEntity> hitList = new ArrayList<LivingEntity>();

		// Add all potential players to the 'hitlist'
		if (targetPlayers.getValue()) {
			for (AbstractClientPlayer entity : MC.level.players()) {
				if (entity == MC.player)
					continue;
				
				if (entity.distanceToSqr(MC.player) >= radiusSqr)
					continue;
				
				if (!shouldTarget(entity))
					continue;
				
				hitList.add(entity);
			}
		}

		// Add all potential mobs to the 'hitlist'
		if (targetAnimals.getValue() || targetMonsters.getValue()) {
			for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
				if (!(entity instanceof LivingEntity living))
					continue;
				
				if (entity instanceof Player)
					continue;
				
				if (entity.distanceToSqr(MC.player) >= radiusSqr)
					continue;
				
				if ((entity instanceof Animal && targetAnimals.getValue())
						|| (entity instanceof Enemy && targetMonsters.getValue())) {
					if (!shouldTarget(living))
						continue;
					
					hitList.add(living);
				}
			}
		}

		// For each entity, get the entity that matches a criteria.
		LivingEntity entityFound = null;
		for (LivingEntity entity : hitList) {
			if (entityFound == null) {
				entityFound = entity;
			} else if (targetPriority.getValue() == TargetPriority.LOWEST_HEALTH) {
				if (entity.getHealth() <= entityFound.getHealth())  {
					entityFound = entity;
				}
			} else if (targetPriority.getValue() == TargetPriority.MOST_HEALTH) {
				if (entity.getHealth() >= entityFound.getHealth()) {
					entityFound = entity;
				}
			} else if (MC.player.distanceToSqr(entity) <= MC.player.distanceToSqr(entityFound)) {
				entityFound = entity;
			}
		}

		// Rotate towards the entity if one is found.
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
		AABB targetBox = new AABB(targetPos.x - 0.1, targetPos.y - 0.1, targetPos.z - 0.1, targetPos.x + 0.1, targetPos.y + 0.1, targetPos.z + 0.1);
		event.getRenderer().drawBox(targetBox, targetColor.getValue(), 1.0f);
	}

	private boolean shouldTarget(LivingEntity entity) {
		if (!EntityUtils.isInFOV(entity, bodyPart.getValue(), fov.getValue()))
			return false;

		if (ignoreDead.getValue() && !entity.isAlive())
			return false;
		
		if (ignoreInvisible.getValue() && entity.isInvisible())
			return false;
		
		if (ignoreSleeping.getValue() && entity.isSleeping())
			return false;
		
		// Attempt to skips NPC and friends.
		if (entity instanceof Player player) {
			if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(player))
				return false;
			if (ignoreNPCs.getValue()) {
				ClientPacketListener connection = MC.getConnection();
				if (connection == null || connection.getPlayerInfo(player.getUUID()) == null)
					return false;
			}
		}
		
		// Perform raycast to skip players not visible.
		if (legit.getValue()) {
			Vec3 eyePos = MC.player.getEyePosition();
			Vec3 targetEyePos = entity.getEyePosition();
			BlockHitResult hit = MC.level.clip(new ClipContext(eyePos, targetEyePos,
					ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, MC.player));
			if (hit.getType() != HitResult.Type.MISS)
				return false;
		}
		return true;
	}
}