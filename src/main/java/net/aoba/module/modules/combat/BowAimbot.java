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
import net.aoba.event.events.SubtickEvent;
import net.aoba.event.listeners.SubtickListener;
import net.aoba.managers.rotation.Rotation;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.RotationGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EntitiesSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.entity.BodyPart;
import net.aoba.utils.entity.EntityUtils;
import net.aoba.utils.entity.TargetPriority;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class BowAimbot extends Module implements SubtickListener {
	private final EntitiesSetting targetEntities = EntitiesSetting.builder().id("bowaimbot_target_entities")
			.displayName("Target Entities")
			.description("Entity types that BowAimbot will target.")
			.defaultValue(Set.of(EntityTypes.PLAYER)).build();

	private final BooleanSetting targetFriends = BooleanSetting.builder().id("bowaimbot_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(false).build();

	private final EnumSetting<TargetPriority> targetPriority = EnumSetting.<TargetPriority>builder()
			.id("bowaimbot_target_priority").displayName("Target Priority")
			.description("The priority used to pick which target to aim towards.")
			.defaultValue(TargetPriority.CLOSEST).build();

	private final EnumSetting<BodyPart> bodyPart = EnumSetting.<BodyPart>builder().id("bowaimbot_body_part")
			.displayName("Body Part").description("The part of the target's body to aim at.")
			.defaultValue(BodyPart.CHEST).build();

	private final BooleanSetting ignoreDead = BooleanSetting.builder().id("bowaimbot_ignore_dead")
			.displayName("Ignore Dead").description("Skip entities that are dead or dying.").defaultValue(true)
			.build();

	private final BooleanSetting ignoreInvisible = BooleanSetting.builder().id("bowaimbot_ignore_invisible")
			.displayName("Ignore Invisible").description("Skip entities that are invisible.").defaultValue(true)
			.build();

	private final BooleanSetting ignoreSleeping = BooleanSetting.builder().id("bowaimbot_ignore_sleeping")
			.displayName("Ignore Sleeping").description("Skip players that are sleeping.").defaultValue(true)
			.build();

	private final BooleanSetting ignoreNPCs = BooleanSetting.builder().id("bowaimbot_ignore_npcs")
			.displayName("Ignore NPCs")
			.description("Attempts to ignore NPCs based on the entity UUID.").defaultValue(true).build();

	private final FloatSetting frequency = FloatSetting.builder().id("bowaimbot_frequency").displayName("Ticks")
			.description("How frequent the aimbot updates (Lower = Laggier)").defaultValue(1.0f).minValue(1.0f)
			.maxValue(20.0f).step(1.0f).build();

	private final FloatSetting predictMovement = FloatSetting.builder().id("bowaimbot_prediction")
			.displayName("Prediction").description("Sets the strength of BowAimbot's movement prediction")
			.defaultValue(2f).minValue(0f).maxValue(10f).step(1f).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("bowaimbot_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.INSTANT).build();

	private final FloatSetting maxRotation = FloatSetting.builder().id("bowaimbot_max_rotation")
			.displayName("Max Rotation").description("The max speed that BowAimbot will rotate").defaultValue(180.0f)
			.minValue(1.0f).maxValue(360.0f).build();

	private final FloatSetting yawRandomness = FloatSetting.builder().id("bowaimbot_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final FloatSetting pitchRandomness = FloatSetting.builder().id("bowaimbot_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final BooleanSetting fakeRotation = BooleanSetting.builder().id("bowaimbot_fake_rotation")
			.displayName("Fake Rotation")
			.description("Spoofs the client's rotation so that the player appears rotated on the server")
			.defaultValue(false).build();

	private final BooleanSetting moveFix = BooleanSetting.builder().id("bowaimbot_move_fix").displayName("Move Fix")
			.description("Corrects movement to match spoofed rotation by using the server yaw for velocity.")
			.defaultValue(false).build();

	private int currentTick = 0;
	private float velocity;
	private double posX;
	private double posY;
	private double posZ;
	private double d;

	public BowAimbot() {
		super("BowAimbot");

		setCategory(Category.of("Combat"));
		setDescription("Calculates the location the crosshair must be to hit an arrow shot.");

		addSetting(targetPriority);
		addSetting(bodyPart);
		addSetting(targetEntities);
		addSetting(targetFriends);
		addSetting(ignoreDead);
		addSetting(ignoreInvisible);
		addSetting(ignoreSleeping);
		addSetting(ignoreNPCs);
		addSetting(frequency);
		addSetting(predictMovement);
		addSetting(rotationMode);
		addSetting(maxRotation);
		addSetting(yawRandomness);
		addSetting(pitchRandomness);
		addSetting(fakeRotation);
		addSetting(moveFix);
	}

	@Override
	public void onDisable() {
		if (Aoba.getInstance().moduleManager.trajectory.state.getValue())
			Aoba.getInstance().moduleManager.trajectory.toggle();
		Aoba.getInstance().eventManager.RemoveListener(SubtickListener.class, this);
	}

	@Override
	public void onEnable() {
		if (!Aoba.getInstance().moduleManager.trajectory.state.getValue())
			Aoba.getInstance().moduleManager.trajectory.toggle();
		Aoba.getInstance().eventManager.AddListener(SubtickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSubtick(SubtickEvent event) {
		currentTick++;

		ItemStack stack = MC.player.getInventory().getSelectedItem();
		Item item = stack.getItem();

		if (!(item instanceof BowItem || item instanceof CrossbowItem)) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return;
		}

		if (item instanceof BowItem && !MC.options.keyUse.isDown() && !MC.player.isUsingItem()) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return;
		}
		if (item instanceof CrossbowItem && !CrossbowItem.isCharged(stack)) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return;
		}

		if (currentTick >= frequency.getValue()) {
			float partialTick = MC.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			Vec3 playerPos = MC.player.getPosition(partialTick);

			velocity = (72000 - MC.player.getUseItemRemainingTicks()) / 20F;
			velocity = (velocity * velocity + velocity * 2) / 3;
			if (velocity > 1)
				velocity = 1;

			ArrayList<LivingEntity> hitList = new ArrayList<LivingEntity>();

			// Add all potential entities to the 'hitlist'
			Set<EntityType<?>> allowed = targetEntities.getValue();
			if (!allowed.isEmpty()) {
				for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
					if (!(entity instanceof LivingEntity living))
						continue;

					if (entity == MC.player)
						continue;

					if (!allowed.contains(entity.getType()))
						continue;

					if (!shouldTarget(living))
						continue;

					hitList.add(living);
				}
			}

			// Pick the best candidate based on priority.
			LivingEntity temp = null;
			for (LivingEntity entity : hitList) {
				if (temp == null) {
					temp = entity;
				} else if (targetPriority.getValue() == TargetPriority.LOWEST_HEALTH) {
					if (entity.getHealth() <= temp.getHealth())
						temp = entity;
				} else if (targetPriority.getValue() == TargetPriority.MOST_HEALTH) {
					if (entity.getHealth() >= temp.getHealth())
						temp = entity;
				} else if (playerPos.distanceToSqr(entity.getPosition(partialTick)) <= playerPos
						.distanceToSqr(temp.getPosition(partialTick))) {
					temp = entity;
				}
			}

			if (temp != null) {
				double hDistance = Math.sqrt(posX * posX + posZ * posZ);
				double hDistanceSq = hDistance * hDistance;
				float g = 0.006F;
				float velocitySq = velocity * velocity;
				float velocityPow4 = velocitySq * velocitySq;

				Vec3 aimPos = EntityUtils.getBodyPartPosition(temp, bodyPart.getValue(), partialTick);

				d = temp.distanceToSqr(MC.player.getEyePosition(partialTick)) * (predictMovement.getValue() / 100);
				posY = aimPos.y + (temp.getY() - temp.yOld) * d - playerPos.y
						- MC.player.getEyeHeight(MC.player.getPose());
				float neededPitch = (float) -Math.toDegrees(
						Math.atan((velocitySq - Math.sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * posY * velocitySq)))
								/ (g * hDistance)));
				posZ = aimPos.z + (temp.getZ() - temp.zOld) * d - playerPos.z;
				posX = aimPos.x + (temp.getX() - temp.xOld) * d - playerPos.x;
				float neededYaw = (float) Math.toDegrees(Math.atan2(posZ, posX)) - 90;

				currentTick = 0;

				Rotation rotation = new Rotation(neededYaw, neededPitch);
				RotationGoal goal = RotationGoal.builder().goal(rotation).mode(rotationMode.getValue())
						.maxRotation(maxRotation.getValue()).yawRandomness(yawRandomness.getValue())
						.pitchRandomness(pitchRandomness.getValue()).fakeRotation(fakeRotation.getValue())
						.moveFix(moveFix.getValue()).build();
				Aoba.getInstance().rotationManager.setGoal(goal);
			} else
				Aoba.getInstance().rotationManager.setGoal(null);
		}
	}

	private boolean shouldTarget(LivingEntity entity) {
		if (ignoreDead.getValue() && !entity.isAlive())
			return false;

		if (ignoreInvisible.getValue() && entity.isInvisible())
			return false;

		if (ignoreSleeping.getValue() && entity.isSleeping())
			return false;

		if (entity instanceof Player player) {
			if (!targetFriends.getValue() && EntityUtils.isFriend(player))
				return false;
			if (ignoreNPCs.getValue() && EntityUtils.isNPC(player))
				return false;
		}
		return true;
	}
}
