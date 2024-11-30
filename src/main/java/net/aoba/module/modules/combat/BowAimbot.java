package net.aoba.module.modules.combat;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.bowaimbot.BowAimbotTargets;
import net.aoba.utils.bowaimbot.BowAimbotUtils;
import net.aoba.utils.render.Render3D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class BowAimbot extends Module implements TickListener, Render3DListener {

	private Entity temp = null;

	private BooleanSetting targetAnimals = BooleanSetting.builder().id("bowaimbot_target_mobs")
			.displayName("Target Mobs").description("Target mobs.").defaultValue(false).build();

	private BooleanSetting targetPlayers = BooleanSetting.builder().id("bowaimbot_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

	private FloatSetting frequency = FloatSetting.builder().id("bowaimbot_frequency").displayName("Ticks")
			.description("How frequent the aimbot updates (Lower = Laggier)").defaultValue(1.0f).minValue(1.0f)
			.maxValue(20.0f).step(1.0f).build();

	private FloatSetting predictMovement = FloatSetting.builder().id("bowaimbot_prediction").displayName("Prediction")
			.description("Sets the strength of BowAimbot's movement prediction").defaultValue(2f).minValue(0f)
			.maxValue(10f).step(1f).build();

	private int currentTick = 0;
	private boolean skip;
	private float velocity;
	private double posX;
	private double posY;
	private double posZ;
	private float neededPitch;
	private double d;
	private float neededYaw;

	public BowAimbot() {
		super("BowAimbot");

		this.setCategory(Category.of("Combat"));
		this.setDescription("Calculates the location the crosshair must be to hit an arrow shot.");

		this.addSetting(targetAnimals);
		this.addSetting(targetPlayers);
		this.addSetting(frequency);
		this.addSetting(predictMovement);
	}

	@Override
	public void onDisable() {
		if (Aoba.getInstance().moduleManager.trajectory.state.getValue())
			Aoba.getInstance().moduleManager.trajectory.toggle();
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
	}

	@Override
	public void onEnable() {
		if (!Aoba.getInstance().moduleManager.trajectory.state.getValue())
			Aoba.getInstance().moduleManager.trajectory.toggle();
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
		skip = false;
		currentTick++;

		ItemStack stack = MC.player.getInventory().getMainHandStack();
		Item item = stack.getItem();
		if (!(item instanceof BowItem || item instanceof CrossbowItem)) {
			temp = null;
			return;
		}

		if (item instanceof BowItem && !MC.options.useKey.isPressed() && !MC.player.isUsingItem()) {
			temp = null;
			return;
		}

		if (item instanceof CrossbowItem && !CrossbowItem.isCharged(stack)) {
			temp = null;
			return;
		}

		velocity = (72000 - MC.player.getItemUseTimeLeft()) / 20F;
		velocity = (velocity * velocity + velocity * 2) / 3;
		if (velocity > 1)
			velocity = 1;

		if (currentTick >= frequency.getValue()) {

			if (targetAnimals.getValue() && targetPlayers.getValue()) {
				if (filterEntities(Stream.of(temp)) == null)
					temp = filterEntities(StreamSupport.stream(MC.world.getEntities().spliterator(), true));
			}

			if (!targetAnimals.getValue() && targetPlayers.getValue()) {
				if (filterPlayers(Stream.of((AbstractClientPlayerEntity) temp)) == null)
					temp = filterPlayers(StreamSupport.stream(MC.world.getPlayers().spliterator(), true));
			}

			if (targetAnimals.getValue() && !targetPlayers.getValue()) {
				if (filterEntities(Stream.of(temp)) == null)
					temp = filterEntities(StreamSupport.stream(MC.world.getEntities().spliterator(), true));
				if (temp instanceof PlayerEntity)
					temp = null;
			}

			if (temp == null)
				return;

			double hDistance = Math.sqrt(posX * posX + posZ * posZ);
			double hDistanceSq = hDistance * hDistance;
			float g = 0.006F;
			float velocitySq = velocity * velocity;
			float velocityPow4 = velocitySq * velocitySq;

			d = temp.squaredDistanceTo(MC.player.getEyePos()) * (predictMovement.getValue() / 100);
			posY = temp.getY() + (temp.getY() - temp.lastRenderY) * d + temp.getHeight() * 0.5 - MC.player.getY()
					- MC.player.getEyeHeight(MC.player.getPose());
			neededPitch = (float) -Math.toDegrees(
					Math.atan((velocitySq - Math.sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * posY * velocitySq)))
							/ (g * hDistance)));
			posZ = temp.getZ() + (temp.getZ() - temp.lastRenderZ) * d - MC.player.getZ();
			posX = temp.getX() + (temp.getX() - temp.lastRenderX) * d - MC.player.getX();
			neededYaw = (float) Math.toDegrees(Math.atan2(posZ, posX)) - 90;

			currentTick = 0;
		}
	}

	private Entity filterEntities(Stream<Entity> s) {
		Stream<Entity> stream = s.filter(BowAimbotTargets.IS_ATTACKABLE);

		return stream.min(Priority.ANGLE_DIST.comparator).orElse(null);
	}

	private Entity filterPlayers(Stream<AbstractClientPlayerEntity> s) {
		Stream<AbstractClientPlayerEntity> stream = s.filter(BowAimbotTargets.IS_ATTACKABLE);

		return stream.min(Priority.ANGLE_DIST.comparator).orElse(null);
	}

	static MinecraftClient MC = AobaClient.MC;

	private enum Priority {
		ANGLE_DIST("", e -> Math.pow(BowAimbotUtils.getAngleToLookVec(e.getBoundingBox().getCenter()), 2)
				+ MC.player.squaredDistanceTo(e));

		private final String name;
		private final Comparator<Entity> comparator;

		private Priority(String name, ToDoubleFunction<Entity> keyExtractor) {
			this.name = name;
			comparator = Comparator.comparingDouble(keyExtractor);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	@Override
	public void onRender(Render3DEvent event) {
		if (skip)
			return;
		if (temp != null) {
			Vec3d offset = Render3D.getEntityPositionOffsetInterpolated(temp,
					event.getRenderTickCounter().getTickDelta(true));
			MC.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES,
					temp.getEyePos().add(offset).add(posX, posY, posZ));
			MC.player.setYaw(neededYaw);
			MC.player.setPitch(neededPitch);
		}
	}
}
