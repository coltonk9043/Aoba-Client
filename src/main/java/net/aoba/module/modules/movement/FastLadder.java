package net.aoba.module.modules.movement;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class FastLadder extends Module implements TickListener {

	private final FloatSetting ladderSpeed = FloatSetting.builder().id("fastladder_speed").displayName("Speed")
			.description("Speed for FastLadder Module.").defaultValue(0.2f).minValue(0.1f).maxValue(0.5f).step(0.1f)
			.build();

	private final FloatSetting accelerationBoost = FloatSetting.builder().id("acceleration_boost")
			.displayName("Acceleration Boost").description("Extra speed applied when moving upwards on ladders.")
			.defaultValue(0.08f).minValue(0.01f).maxValue(0.2f).step(0.01f).build();

	private final FloatSetting decelerationPenalty = FloatSetting.builder().id("deceleration_penalty")
			.displayName("Deceleration Penalty")
			.description("Speed reduction when moving sideways or not moving on ladders.").defaultValue(0.08f)
			.minValue(0.01f).maxValue(0.2f).step(0.01f).build();

	public FastLadder() {
		super("FastLadder");
		this.setCategory(Category.of("Movement"));
		this.setDescription("Allows players to climb up Ladders faster");

		this.addSetting(ladderSpeed);
		this.addSetting(accelerationBoost);
		this.addSetting(decelerationPenalty);
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
		ClientPlayerEntity player = MC.player;

		if (!player.isClimbing() || !player.horizontalCollision)
			return;

		if (player.input.movementForward == 0 && player.input.movementSideways == 0)
			return;

		Vec3d velocity = player.getVelocity();
		double yVelocity = ladderSpeed.getValue() + accelerationBoost.getValue();

		if (player.input.movementForward == 0 && player.input.movementSideways != 0) {
			yVelocity -= decelerationPenalty.getValue();
		}

		player.setVelocity(velocity.x, yVelocity, velocity.z);
	}

	@Override
	public void onTick(Post event) {

	}
}
