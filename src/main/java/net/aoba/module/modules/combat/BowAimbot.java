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
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.managers.rotation.Rotation;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.RotationGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.entity.BodyPart;
import net.aoba.utils.entity.EntityUtils;
import net.aoba.utils.entity.TargetPriority;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class BowAimbot extends Module implements TickListener {
	private final BooleanSetting targetAnimals = BooleanSetting.builder().id("bowaimbot_target_animals")
			.displayName("Target Animals").description("Target animals.").defaultValue(false).build();

	private final BooleanSetting targetMonsters = BooleanSetting.builder().id("bowaimbot_target_monsters")
			.displayName("Target Monsters").description("Target Monsters.").defaultValue(true).build();

	private final BooleanSetting targetPlayers = BooleanSetting.builder().id("bowaimbot_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

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
		addSetting(targetAnimals);
		addSetting(targetMonsters);
		addSetting(targetPlayers);
		addSetting(targetFriends);
		addSetting(ignoreDead);
		addSetting(ignoreInvisible);
		addSetting(ignoreSleeping);
		addSetting(ignoreNPCs);
		addSetting(frequency);
		addSetting(predictMovement);
	}

	@Override
	public void onDisable() {
		if (Aoba.getInstance().moduleManager.trajectory.state.getValue())
			Aoba.getInstance().moduleManager.trajectory.toggle();
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		if (!Aoba.getInstance().moduleManager.trajectory.state.getValue())
			Aoba.getInstance().moduleManager.trajectory.toggle();
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
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
			velocity = (72000 - MC.player.getUseItemRemainingTicks()) / 20F;
			velocity = (velocity * velocity + velocity * 2) / 3;
			if (velocity > 1)
				velocity = 1;

			ArrayList<LivingEntity> hitList = new ArrayList<LivingEntity>();

			// Add all potential animals/monsters to the 'hitlist'
			if (targetAnimals.getValue() || targetMonsters.getValue()) {
				for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
					if (!(entity instanceof LivingEntity living))
						continue;

					boolean matchesAnimal = targetAnimals.getValue() && living instanceof Animal;
					boolean matchesMonster = targetMonsters.getValue() && living instanceof Enemy;
					if (!matchesAnimal && !matchesMonster)
						continue;

					if (!shouldTarget(living))
						continue;

					hitList.add(living);
				}
			}

			// Add all potential players to the 'hitlist'
			if (targetPlayers.getValue()) {
				for (Player player : Aoba.getInstance().entityManager.getPlayers()) {
					if (player == MC.player)
						continue;

					if (!shouldTarget(player))
						continue;

					hitList.add(player);
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
				} else if (MC.player.distanceToSqr(entity) <= MC.player.distanceToSqr(temp)) {
					temp = entity;
				}
			}

			if (temp != null) {
				double hDistance = Math.sqrt(posX * posX + posZ * posZ);
				double hDistanceSq = hDistance * hDistance;
				float g = 0.006F;
				float velocitySq = velocity * velocity;
				float velocityPow4 = velocitySq * velocitySq;

				Vec3 aimPos = EntityUtils.getBodyPartPosition(temp, bodyPart.getValue(), 1.0f);

				d = temp.distanceToSqr(MC.player.getEyePosition()) * (predictMovement.getValue() / 100);
				posY = aimPos.y + (temp.getY() - temp.yOld) * d - MC.player.getY()
						- MC.player.getEyeHeight(MC.player.getPose());
				float neededPitch = (float) -Math.toDegrees(
						Math.atan((velocitySq - Math.sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * posY * velocitySq)))
								/ (g * hDistance)));
				posZ = aimPos.z + (temp.getZ() - temp.zOld) * d - MC.player.getZ();
				posX = aimPos.x + (temp.getX() - temp.xOld) * d - MC.player.getX();
				float neededYaw = (float) Math.toDegrees(Math.atan2(posZ, posX)) - 90;

				currentTick = 0;

				Rotation rotation = new Rotation(neededYaw, neededPitch);
				RotationGoal goal = RotationGoal.builder().goal(rotation).mode(RotationMode.INSTANT).build();
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
