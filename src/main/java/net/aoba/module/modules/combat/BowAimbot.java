/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.managers.rotation.Rotation;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.RotationGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.entity.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class BowAimbot extends Module implements TickListener {
	private final BooleanSetting targetAnimals = BooleanSetting.builder().id("bowaimbot_target_mobs")
			.displayName("Target Mobs").description("Target mobs.").defaultValue(false).build();

	private final BooleanSetting targetPlayers = BooleanSetting.builder().id("bowaimbot_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

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

		addSetting(targetAnimals);
		addSetting(targetPlayers);
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

		ItemStack stack = MC.player.getInventory().getSelectedStack();
		Item item = stack.getItem();

		if (!(item instanceof BowItem || item instanceof CrossbowItem)) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return;
		}

		if (item instanceof BowItem && !MC.options.useKey.isPressed() && !MC.player.isUsingItem()) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return;
		}
		if (item instanceof CrossbowItem && !CrossbowItem.isCharged(stack)) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return;
		}

		if (currentTick >= frequency.getValue()) {
			velocity = (72000 - MC.player.getItemUseTimeLeft()) / 20F;
			velocity = (velocity * velocity + velocity * 2) / 3;
			if (velocity > 1)
				velocity = 1;

			Entity temp = null;
			if (targetAnimals.getValue() && targetPlayers.getValue()) {
				if (filterEntities(Stream.of(temp)) == null)
					temp = filterEntities(
							StreamSupport.stream(Aoba.getInstance().entityManager.getEntities().spliterator(), true));
			}

			if (!targetAnimals.getValue() && targetPlayers.getValue()) {
				if (filterPlayers(Stream.of((PlayerEntity) temp)) == null)
					temp = filterPlayers(
							StreamSupport.stream(Aoba.getInstance().entityManager.getPlayers().spliterator(), true));
			}

			if (targetAnimals.getValue() && !targetPlayers.getValue()) {
				if (filterEntities(Stream.of(temp)) == null)
					temp = filterEntities(
							StreamSupport.stream(Aoba.getInstance().entityManager.getEntities().spliterator(), true));
				if (temp instanceof PlayerEntity)
					temp = null;
			}

			if (temp != null) {
				double hDistance = Math.sqrt(posX * posX + posZ * posZ);
				double hDistanceSq = hDistance * hDistance;
				float g = 0.006F;
				float velocitySq = velocity * velocity;
				float velocityPow4 = velocitySq * velocitySq;

				d = temp.squaredDistanceTo(MC.player.getEyePos()) * (predictMovement.getValue() / 100);
				posY = temp.getY() + (temp.getY() - temp.lastRenderY) * d + temp.getHeight() * 0.5 - MC.player.getY()
						- MC.player.getEyeHeight(MC.player.getPose());
				float neededPitch = (float) -Math.toDegrees(
						Math.atan((velocitySq - Math.sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * posY * velocitySq)))
								/ (g * hDistance)));
				posZ = temp.getZ() + (temp.getZ() - temp.lastRenderZ) * d - MC.player.getZ();
				posX = temp.getX() + (temp.getX() - temp.lastRenderX) * d - MC.player.getX();
				float neededYaw = (float) Math.toDegrees(Math.atan2(posZ, posX)) - 90;

				currentTick = 0;

				Rotation rotation = new Rotation(neededYaw, neededPitch);
				RotationGoal goal = RotationGoal.builder().goal(rotation).mode(RotationMode.INSTANT).build();
				Aoba.getInstance().rotationManager.setGoal(goal);
			} else
				Aoba.getInstance().rotationManager.setGoal(null);
		}
	}

	private Entity filterEntities(Stream<Entity> s) {
		Stream<Entity> stream = s.filter(EntityUtils.IS_ATTACKABLE);
		return stream.min(Priority.ANGLE_DIST.comparator).orElse(null);
	}

	private Entity filterPlayers(Stream<PlayerEntity> s) {
		Stream<PlayerEntity> stream = s.filter(EntityUtils.IS_ATTACKABLE);
		return stream.min(Priority.ANGLE_DIST.comparator).orElse(null);
	}

	private enum Priority {
		ANGLE_DIST("",
				e -> Math.pow(getAngleToLookVec(e.getBoundingBox().getCenter()), 2) + MC.player.squaredDistanceTo(e));

		private final String name;
		private final Comparator<Entity> comparator;

		Priority(String name, ToDoubleFunction<Entity> keyExtractor) {
			this.name = name;
			comparator = Comparator.comparingDouble(keyExtractor);
		}

		@Override
		public String toString() {
			return name;
		}

		public static double getAngleToLookVec(Vec3d vec) {
			Rotation rotation = Rotation.getPlayerRotationDeltaFromPosition(vec);
			return rotation.magnitude();
		}
	}
}
