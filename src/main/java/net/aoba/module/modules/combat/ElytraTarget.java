package net.aoba.module.modules.combat;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.EntityGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.FindItemResult;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Items;

public class ElytraTarget extends Module implements TickListener {

	private LivingEntity target = null;
	private int currentFireworkTick = 0;
	private int currentAimTick = 0;
	private int swapBackDelayTick = 0;
	private int useDelayTick = -1;
	private int previousSlot = -1;
	
	private final BooleanSetting targetFriends = BooleanSetting.builder().id("elytratarget_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(true).build();

	private final FloatSetting interval = FloatSetting.builder().id("elytratarget_interval").displayName("Fireworks Interval")
			.description("How often to use fireworks").defaultValue(10.0f).minValue(1.0f)
			.maxValue(40.0f).step(1.0f).build();
	
	private final FloatSetting frequency = FloatSetting.builder().id("elytratarget_frequency").displayName("Ticks")
			.description("How frequent the aimbot updates (Lower = Laggier)").defaultValue(1.0f).minValue(1.0f)
			.maxValue(20.0f).step(1.0f).build();

	private final FloatSetting radius = FloatSetting.builder().id("elytratarget_radius").displayName("Radius")
			.description("Radius that the aimbot will lock onto a target.").defaultValue(64.0f).minValue(1.0f)
			.maxValue(256.0f).step(1.0f).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("elytratarget_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.SMOOTH).build();

	private final FloatSetting maxRotation = FloatSetting.builder().id("elytratarget_max_rotation")
			.displayName("Max Rotation").description("The max speed that Aimbot will rotate").defaultValue(50.0f)
			.minValue(1.0f).maxValue(360.0f).build();

	private final FloatSetting yawRandomness = FloatSetting.builder().id("elytratarget_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.5f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final FloatSetting pitchRandomness = FloatSetting.builder().id("elytratarget_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.3f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final BooleanSetting fakeRotation = BooleanSetting.builder().id("elytratarget_fake_rotation")
			.displayName("Fake Rotation")
			.description("Spoofs the client's rotation so that the player appears rotated on the server")
			.defaultValue(false).build();
	
	private final BooleanSetting legit = BooleanSetting.builder().id("elytratarget_legit")
			.displayName("Legit")
			.description("Whether the player must be visible to fly to.")
			.defaultValue(true).build();

	private final FloatSetting useDelay = FloatSetting.builder().id("elytratarget_use_delay")
			.displayName("Use Delay")
			.description("Delay in ticks between swapping to fireworks and using them.")
			.defaultValue(3.0f).minValue(1.0f).maxValue(10.0f).step(1.0f).build();

	private final FloatSetting swapDelay = FloatSetting.builder().id("elytratarget_swap_delay")
			.displayName("Swap Delay")
			.description("Delay in ticks between using the firework and swapping back.")
			.defaultValue(4.0f).minValue(3.0f).maxValue(10.0f).step(1.0f).build();

	private final BooleanSetting moveFix = BooleanSetting.builder().id("elytratarget_move_fix")
			.displayName("Move Fix")
			.description("Corrects movement to match spoofed rotation by using the server yaw for velocity.")
			.defaultValue(false).build();


	public ElytraTarget() {
		super("ElytraTarget");
		setCategory(Category.of("Combat"));
		setDescription("Locks your crosshair towards a desired player or entity and uses fireworks to fly towards them.");
		addSetting(targetFriends);
		addSetting(interval);
		addSetting(frequency);
		addSetting(radius);
		addSetting(rotationMode);
		addSetting(maxRotation);
		addSetting(yawRandomness);
		addSetting(pitchRandomness);
		addSetting(fakeRotation);
		addSetting(legit);
		addSetting(useDelay);
		addSetting(swapDelay);
		addSetting(moveFix);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().rotationManager.setGoal(null);
		currentFireworkTick = 0;
		swapBackDelayTick = 0;
		useDelayTick = -1;
		if (previousSlot != -1) {
			swap(previousSlot, false);
			previousSlot = -1;
		}
	}

	@Override
	public void onEnable() {
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
		boolean flying = MC.player.isFallFlying() && MC.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);

		// Swap back to the player's old hand when they are not flying anymore.
		if (!flying && previousSlot != -1) {
			swap(previousSlot, false);
			previousSlot = -1;
			useDelayTick = -1;
			swapBackDelayTick = 0;
			return;
		}

		// Return early if the player is not flying.
		if (!flying) {
			return;
		}

		// Aim at the target.
		currentAimTick++;
		if (currentAimTick >= frequency.getValue()) {
			target = null;
			float radiusSqr = radius.getValueSqr();

			// Find the closest player within range.
			for (AbstractClientPlayer entity : MC.level.players()) {
				if (entity == MC.player)
					continue;

				if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(entity))
					continue;

				double entityDistanceToPlayer = entity.distanceToSqr(MC.player);
				if (entityDistanceToPlayer >= radiusSqr)
					continue;

				if (target == null || entityDistanceToPlayer < target.distanceToSqr(MC.player)) {
					target = entity;
				}
			}

			if (target != null) {
				EntityGoal rotation = EntityGoal.builder().goal(target).mode(rotationMode.getValue())
						.maxRotation(maxRotation.getValue()).pitchRandomness(pitchRandomness.getValue())
						.yawRandomness(yawRandomness.getValue()).fakeRotation(fakeRotation.getValue())
						.moveFix(moveFix.getValue()).build();
				Aoba.getInstance().rotationManager.setGoal(rotation);
			} else {
				Aoba.getInstance().rotationManager.setGoal(null);
			}

			currentAimTick = 0;
		}

		// We have previously swapped to the fireworks, so now we wait
		// until the value of useDelay amount of ticks have passed.
		if (previousSlot != -1 && useDelayTick >= 0) {
			useDelayTick++;
			if (useDelayTick >= useDelay.getValue()) {
				MC.gameMode.useItem(MC.player, InteractionHand.MAIN_HAND);
				useDelayTick = -1;
				swapBackDelayTick = 0;
			}
			return;
		}

		// If waiting to swap back, count down the delay before swapping back to the old slot.
		if (previousSlot != -1) {
			swapBackDelayTick++;
			if (swapBackDelayTick >= swapDelay.getValue()) {
				swap(previousSlot, false);
				previousSlot = -1;
				swapBackDelayTick = 0;
			}
			return;
		}

		if (target != null) {
			// Check if legit is disabled OR if enabled, check if the target is within the player's line of sight.
			if (!legit.getValue() || MC.player.hasLineOfSight(target)) {
				if (currentFireworkTick >= interval.getValue()) {
					FindItemResult findItemResult = findInHotbar(s -> s.getItem() instanceof FireworkRocketItem);
					if (findItemResult.found()) {
						previousSlot = MC.player.getInventory().getSelectedSlot();
						swap(findItemResult.slot(), false);
						useDelayTick = 0;
					}
					currentFireworkTick = 0;
				}
			}
		}
		currentFireworkTick++;
	}
}