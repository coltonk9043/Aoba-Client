package net.aoba.module.modules.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.mutable.MutableObject;
import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Colors;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.Vec3dGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.rendering.shaders.Shader;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.ShaderSetting;
import net.aoba.utils.BlockPlacement;
import net.aoba.utils.FindItemResult;
import net.aoba.utils.entity.DamageUtils;
import net.aoba.utils.entity.TargetPriority;
import net.aoba.utils.player.InteractionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AutoAnchor extends Module implements TickListener, Render3DListener {

	private final FloatSetting radius = FloatSetting.builder().id("autoanchor_radius").displayName("Radius")
			.description("Radius that the anchor will be placed / activated.").defaultValue(3.0f).minValue(1.0f)
			.maxValue(15.0f).step(0.1f).build();

	private final ShaderSetting blockColor = ShaderSetting.builder().id("autoanchor_color").displayName("Color")
			.description("The color of the placement position of respawn anchors.")
			.defaultValue(Shader.solid(Colors.Red)).build();

	private final FloatSetting chargeDelay = FloatSetting.builder().id("autoanchor_charge_delay")
			.displayName("Charge Delay").description("Delay between anchor activation in milliseconds.")
			.defaultValue(500f).minValue(0f).maxValue(2000f).step(50f).build();

	private final FloatSetting placeDelay = FloatSetting.builder().id("autoanchor_place_delay")
			.displayName("Place Delay").description("Delay between placing anchors in milliseconds.").defaultValue(500f)
			.minValue(0f).maxValue(2000f).step(50f).build();

	private final BooleanSetting autoPlace = BooleanSetting.builder().id("autoanchor_autoplace")
			.displayName("AutoPlace")
			.description("Automatically places the anchor near players within a radius from the player.")
			.defaultValue(true).build();

	private final BooleanSetting autoActivate = BooleanSetting.builder().id("autoanchor_auto_activate")
			.displayName("AutoActivate")
			.description(
					"Automatically charges and detonates anchors. If disabled, anchors are placed but left for manual activation.")
			.defaultValue(true).build();

	private final BooleanSetting autoSwitch = BooleanSetting.builder().id("autoanchor_auto_switch")
			.displayName("Auto Switch").description("Automatically switch to Respawn Anchor / Glowstone.")
			.defaultValue(true).build();

	private final BooleanSetting safeAnchor = BooleanSetting.builder().id("autoanchor_safe_anchor")
			.displayName("SafeAnchor").description("Places a block in front of the player to reduce damage.")
			.defaultValue(true).build();

	private final BooleanSetting antiSuicide = BooleanSetting.builder().id("autoanchor_anti_suicide")
			.displayName("AntiSuicide").description("Prevents activating anchors if it would result in player's death.")
			.defaultValue(true).build();

	private final FloatSetting minDamage = FloatSetting.builder().id("autoanchor_min_damage").displayName("Min Damage")
			.description("Minimum damage an anchor must deal to be placed or activated.").defaultValue(6f).minValue(0f)
			.maxValue(36f).step(0.5f).build();

	private final FloatSetting maxSelfDamage = FloatSetting.builder().id("autoanchor_max_self_damage")
			.displayName("Max Self Damage")
			.description("Maximum self-damage the player can take from a single anchor activation.").defaultValue(4f)
			.minValue(0f).maxValue(20f).step(0.5f).build();

	private final FloatSetting enemyRange = FloatSetting.builder().id("autoanchor_enemy_range")
			.displayName("Enemy Range").description("Maximum distance an enemy can be to be considered a target.")
			.defaultValue(12f).minValue(0f).maxValue(32f).step(1f).build();

	private final BooleanSetting ignoreWalls = BooleanSetting.builder().id("autoanchor_ignore_walls")
			.displayName("Ignore Walls").description("Ignore walls when targeting enemies.").defaultValue(true).build();

	private final EnumSetting<TargetPriority> targetPriority = EnumSetting.<TargetPriority>builder()
			.id("autoanchor_target_priority").displayName("Target Priority").description("Priority to target players.")
			.defaultValue(TargetPriority.CLOSEST).build();

	private final BooleanSetting targetAnimals = BooleanSetting.builder().id("autoanchor_target_mobs")
			.displayName("Target Mobs").description("Target mobs.").defaultValue(false).build();

	private final BooleanSetting targetPlayers = BooleanSetting.builder().id("autoanchor_target_players")
			.displayName("Target Players").description("Target Players.").defaultValue(true).build();

	private final BooleanSetting targetFriends = BooleanSetting.builder().id("autoanchor_target_friends")
			.displayName("Target Friends").description("Target Friends.").defaultValue(true).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("autoanchor_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private final BooleanSetting legit = BooleanSetting.builder().id("autoanchor_legit").displayName("Legit")
			.description(
					"Whether a raycast will be used to ensure that auto anchor is aiming at the target positions before placing/activating the anchor.")
			.defaultValue(true).build();

	private final FloatSetting maxRotation = FloatSetting.builder().id("autoanchor_max_rotation")
			.displayName("Max Rotation").description("The max speed that Aimbot will rotate").defaultValue(10.0f)
			.minValue(1.0f).maxValue(360.0f).build();

	private final FloatSetting yawRandomness = FloatSetting.builder().id("autoanchor_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final FloatSetting pitchRandomness = FloatSetting.builder().id("autoanchor_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final BooleanSetting fakeRotation = BooleanSetting.builder().id("autoanchor_fake_rotation")
			.displayName("Fake Rotation")
			.description("Spoofs the client's rotation so that the player appears rotated on the server")
			.defaultValue(false).build();

	private final BooleanSetting moveFix = BooleanSetting.builder().id("autoanchor_move_fix").displayName("Move Fix")
			.description("Corrects movement to match spoofed rotation by using the server yaw for velocity.")
			.defaultValue(false).build();

	private static final long BOX_DISPLAY_TIME_MS = 3000;

	private long lastChargeTime;
	private long lastPlaceTime;
	private long lastShieldTime;
	private final Map<BlockPos, Long> displayedBoxes = new HashMap<>();

	public AutoAnchor() {
		super("AutoAnchor");
		setCategory(Category.of("Combat"));
		setDescription("Automatically places and charges a respawn anchor.");

		addSetting(radius);
		addSetting(blockColor);
		addSetting(autoPlace);
		addSetting(autoActivate);
		addSetting(autoSwitch);
		addSetting(safeAnchor);
		addSetting(chargeDelay);
		addSetting(placeDelay);

		addSetting(antiSuicide);
		addSetting(minDamage);
		addSetting(maxSelfDamage);
		addSetting(enemyRange);
		addSetting(ignoreWalls);

		addSetting(targetPriority);
		addSetting(targetAnimals);
		addSetting(targetPlayers);
		addSetting(targetFriends);

		addSetting(rotationMode);
		addSetting(legit);
		addSetting(maxRotation);
		addSetting(yawRandomness);
		addSetting(pitchRandomness);
		addSetting(fakeRotation);
		addSetting(moveFix);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().rotationManager.setGoal(null);
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
		long currentTime = System.currentTimeMillis();

		// Get all targets within the radius
		double rangeSq = enemyRange.getValueSqr();
		List<LivingEntity> targets = new ArrayList<>();
		for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {
			if (entity == MC.player)
				continue;

			if (!(entity instanceof LivingEntity living))
				continue;

			if (MC.player.distanceToSqr(living) > rangeSq)
				continue;

			// Conditionally skip players or friends.
			if (living instanceof Player playerEntity) {
				if (!targetPlayers.getValue()
						|| (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(playerEntity))) {
					continue;
				}
			} else {
				if (!targetAnimals.getValue()) {
					continue;
				}
			}
			targets.add(living);
		}

		// Do nothing if no targets are in range.
		if (targets.isEmpty()) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return;
		}

		// Find a nearby anchor. If there is no anchor, try to place one.
		BlockPos anchor = InteractionUtils.findNearestBlockOf(Blocks.RESPAWN_ANCHOR, radius.getValue());
		if (anchor == null) {
			BlockPlacement chosenPlacement = pickPlacement(targets);
			float placeDelayVal = placeDelay.getValue();
			boolean canPlaceNow = autoPlace.getValue()
					&& (placeDelayVal <= 0 || currentTime - lastPlaceTime >= placeDelayVal);

			if (canPlaceNow && chosenPlacement != null) {
				if (placeAnchor(chosenPlacement)) {
					lastPlaceTime = currentTime;
				}
			} else if (rotationMode.getValue() != RotationMode.NONE && chosenPlacement != null) {
				// Aim at the best location so that the player can place manually
				Vec3 clickVec = Vec3.atCenterOf(chosenPlacement.placementPos()).add(
						chosenPlacement.placementFace().getStepX() * 0.5,
						chosenPlacement.placementFace().getStepY() * 0.5,
						chosenPlacement.placementFace().getStepZ() * 0.5);
				Aoba.getInstance().rotationManager.setGoal(Vec3dGoal.builder().goal(clickVec)
						.mode(rotationMode.getValue()).maxRotation(maxRotation.getValue())
						.pitchRandomness(pitchRandomness.getValue()).yawRandomness(yawRandomness.getValue())
						.fakeRotation(fakeRotation.getValue()).moveFix(moveFix.getValue()).build());
			} else {
				Aoba.getInstance().rotationManager.setGoal(null);
			}
			return;
		}

		// If auto-activation is disabled, rotate towards the closest anchor.
		if (!autoActivate.getValue()) {
			if (rotationMode.getValue() != RotationMode.NONE) {
				Direction clickFace = InteractionUtils.getClosestDirection(anchor);
				if (clickFace != null) {
					Vec3 hitPos = Vec3.atCenterOf(anchor).add(clickFace.getStepX() * 0.5, clickFace.getStepY() * 0.5,
							clickFace.getStepZ() * 0.5);
					Aoba.getInstance().rotationManager.setGoal(Vec3dGoal.builder().goal(hitPos)
							.mode(rotationMode.getValue()).maxRotation(maxRotation.getValue())
							.pitchRandomness(pitchRandomness.getValue()).yawRandomness(yawRandomness.getValue())
							.fakeRotation(fakeRotation.getValue()).moveFix(moveFix.getValue()).build());
				} else {
					Aoba.getInstance().rotationManager.setGoal(null);
				}
			} else {
				Aoba.getInstance().rotationManager.setGoal(null);
			}
			return;
		}

		// Skip the existing anchor entirely if it's too far from every target to deal
		// real damage.
		if (!willDamageAnyTarget(anchor, targets)) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return;
		}

		// Place a shield if we're about to detonate and the path isn't already
		// shielded.
		int charge = MC.level.getBlockState(anchor).getValue(RespawnAnchorBlock.CHARGE);
		boolean willDetonate = charge >= RespawnAnchorBlock.MAX_CHARGES;
		if (willDetonate && safeAnchor.getValue() && !isShielded(anchor)) {
			BlockPos shieldPos = findShieldPosition(anchor);
			if (shieldPos != null) {
				if (placeShield(shieldPos))
					lastShieldTime = currentTime;
				return;
			}
		}

		// Do not detonate if the detonation will cause the player to die.
		if (willDetonate && antiSuicide.getValue()) {
			double selfDamage = DamageUtils.anchorDamage(MC.player, anchor.getCenter());
			if (selfDamage > maxSelfDamage.getValue()) {
				Aoba.getInstance().rotationManager.setGoal(null);
				return;
			}
		}

		// Skip if the anchor is behind a wall.
		if (!ignoreWalls.getValue() && InteractionUtils.isBehindWall(anchor)) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return;
		}

		// Click the anchor (server interprets it as charge or detonation depending on
		// charge state).
		float chargeDelayVal = chargeDelay.getValue();
		if (chargeDelayVal <= 0 || currentTime - lastChargeTime >= chargeDelayVal) {
			if (chargeAnchor(anchor))
				lastChargeTime = currentTime;
		}
	}

	@Override
	public void onTick(TickEvent.Post event) {
	}

	/**
	 * Attempts to place a new respawn anchor at the chosen placement.
	 *
	 * @param chosenPlacement Pre-computed placement to use.
	 * @return True if an anchor is placed, false otherwise.
	 */
	private boolean placeAnchor(BlockPlacement chosenPlacement) {
		// Find the Respawn Anchor item in the hotbar.
		FindItemResult result = find(Items.RESPAWN_ANCHOR);
		if (!result.found() || !result.isHotbar()) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return false;
		}

		// Get the placement position in world space
		BlockPos placementPos = chosenPlacement.placementPos();
		Direction placementFace = chosenPlacement.placementFace();
		BlockPos targetPos = chosenPlacement.targetPos();

		Vec3 targetVec = Vec3.atCenterOf(placementPos).add(placementFace.getStepX() * 0.5,
				placementFace.getStepY() * 0.5, placementFace.getStepZ() * 0.5);

		Aoba.getInstance().rotationManager.setGoal(
				Vec3dGoal.builder().goal(targetVec).mode(rotationMode.getValue()).maxRotation(maxRotation.getValue())
						.pitchRandomness(pitchRandomness.getValue()).yawRandomness(yawRandomness.getValue())
						.fakeRotation(fakeRotation.getValue()).moveFix(moveFix.getValue()).build());

		// Check crosshair raycast if legit enabled.
		BlockHitResult hit;
		if (legit.getValue()) {
			hit = InteractionUtils.raycastBlock(placementPos, placementFace);
		} else
			hit = new BlockHitResult(targetVec, placementFace, placementPos, false);

		// Place block if raycast is valid.
		if (hit == null)
			return false;

		// Swap only after we've confirmed we'll actually click.
		if (autoSwitch.getValue())
			swap(result.slot(), false);

		MC.gameMode.useItemOn(MC.player, InteractionHand.MAIN_HAND, hit);
		MC.player.connection.send(
				new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, 0, MC.player.getYRot(), MC.player.getXRot()));
		MC.player.swing(InteractionHand.MAIN_HAND);

		displayedBoxes.put(targetPos, System.currentTimeMillis());
		return true;
	}

	/**
	 * Returns the best possible block placement for a respawn anchor based on the
	 * target priority and list of available targets.
	 * 
	 * @param targetsInRange List of available targets.
	 * @return Placement record containing placement information.
	 */
	private BlockPlacement pickPlacement(List<LivingEntity> targetsInRange) {
		BlockPlacement chosenPlacement = null;
		double chosenDamage = 0;
		double chosenDistanceSq = Double.MAX_VALUE;
		double chosenHealth = Double.MAX_VALUE;
		double maxSelfDamageAllowed = maxSelfDamage.getValue();
		double minDamageRequired = minDamage.getValue();

		for (LivingEntity target : targetsInRange) {
			double distanceSquared = MC.player.distanceToSqr(target);

			// Find the optimal placement for a given target.
			Optional<BlockPlacement> candidate = findPlacement(target);
			if (candidate.isEmpty())
				continue;
			BlockPlacement placement = candidate.get();

			// Calculate the explosion damage and check if it deals the minimum allowable
			// damage. Check whether the explosion will also kill the player.
			Vec3 explosionPos = placement.targetPos().getCenter();
			double damage = DamageUtils.anchorDamage(target, explosionPos);
			if (damage < minDamageRequired)
				continue;

			if (antiSuicide.getValue()) {
				double selfDamage = DamageUtils.anchorDamage(MC.player, explosionPos);
				if (selfDamage > maxSelfDamageAllowed)
					continue;
			}

			// Get whether the placement is better than the previous.
			boolean isPlacementBetter;
			switch (targetPriority.getValue()) {
			case CLOSEST:
				isPlacementBetter = chosenPlacement == null || distanceSquared < chosenDistanceSq;
				break;
			case HIGHEST_DAMAGE:
				isPlacementBetter = chosenPlacement == null || damage > chosenDamage;
				break;
			case LOWEST_HEALTH:
				isPlacementBetter = chosenPlacement == null || target.getHealth() < chosenHealth;
				break;
			case MOST_HEALTH:
				isPlacementBetter = chosenPlacement == null || target.getHealth() > chosenHealth;
				break;
			default:
				isPlacementBetter = false;
				break;
			}

			if (isPlacementBetter) {
				chosenPlacement = placement;
				chosenDamage = damage;
				chosenDistanceSq = distanceSquared;
				chosenHealth = target.getHealth();
			}
		}

		return chosenPlacement;
	}

	/**
	 * Calculates whether an anchor placed at the anchorPos will cause any damage to
	 * the targets.
	 * 
	 * @param anchorPos The position to place the anchor.
	 * @param targets   List of the targets to calculate.
	 * @return True if it will do damage to any target, false otherwise.
	 */
	private boolean willDamageAnyTarget(BlockPos anchorPos, List<LivingEntity> targets) {
		Vec3 explosionPos = anchorPos.getCenter();
		double minDamageRequired = minDamage.getValue();
		for (LivingEntity target : targets) {
			if (DamageUtils.anchorDamage(target, explosionPos) >= minDamageRequired)
				return true;
		}
		return false;
	}

	/**
	 * Finds the best respawn anchor placement for a given target to maximize dmg.
	 * 
	 * @param target Target to place anchor next to.
	 * @return Returns the optimal block placement if one exists.
	 */
	private Optional<BlockPlacement> findPlacement(LivingEntity target) {
		BlockPos playerBlockPos = MC.player.blockPosition();
		Vec3 playerPos = MC.player.position();
		BlockPos targetBlockPos = target.blockPosition();

		double radiusSqr = radius.getValueSqr();
		double maxDamage = 0;
		double minDistance = Double.MAX_VALUE;

		// Scan each block in the radius.
		BlockPlacement best = null;
		int blockScanExtent = (int) Math.ceil(radius.getValue());
		for (int x = -blockScanExtent; x <= blockScanExtent; x++) {
			for (int y = -blockScanExtent; y <= blockScanExtent; y++) {
				for (int z = -blockScanExtent; z <= blockScanExtent; z++) {

					// Skip if the distance is too far.
					BlockPos anchorPos = playerBlockPos.offset(x, y, z);
					if (anchorPos.getCenter().distanceToSqr(playerPos) > radiusSqr)
						continue;

					// Skip if can't be replaced.
					if (!MC.level.getBlockState(anchorPos).canBeReplaced())
						continue;

					// Check whether it is obstructed.
					AABB anchorBox = new AABB(anchorPos);
					boolean obstructed = false;
					for (Player p : MC.level.players()) {
						if (p.getBoundingBox().intersects(anchorBox)) {
							obstructed = true;
							break;
						}
					}
					if (obstructed)
						continue;

					// Find the face to place on.
					BlockPos placementBlockPos = null;
					Direction placementFace = null;
					for (Direction d : Direction.values()) {
						BlockPos neighbor = anchorPos.relative(d);
						if (!MC.level.getBlockState(neighbor).blocksMotion())
							continue;
						if (d == Direction.DOWN) {
							placementBlockPos = neighbor;
							placementFace = Direction.UP;
							break;
						}
						if (placementBlockPos == null) {
							placementBlockPos = neighbor;
							placementFace = d.getOpposite();
						}
					}
					if (placementBlockPos == null)
						continue;

					// Skip if the support block has no reachable face (wall in the way).
					if (!ignoreWalls.getValue() && InteractionUtils.isBehindWall(placementBlockPos))
						continue;

					// Calculate the explosion damage of the position.
					Vec3 explosionPos = anchorPos.getCenter();
					double explosionDmg = DamageUtils.anchorDamage(target, explosionPos);
					double distance = targetBlockPos.distSqr(anchorPos);

					if (explosionDmg > maxDamage || (explosionDmg == maxDamage && distance < minDistance)) {
						maxDamage = explosionDmg;
						minDistance = distance;
						best = new BlockPlacement(anchorPos, placementBlockPos, placementFace);
					}
				}
			}
		}

		return Optional.ofNullable(best);
	}

	/**
	 * Attempts to charge an anchor at a given position.
	 * 
	 * @param anchorPos Position of the respawn anchor.
	 * @return True if the anchor was charged, false otherwise.
	 */
	private boolean chargeAnchor(BlockPos anchorPos) {
		// Get nearest face.
		Direction clickFace = InteractionUtils.getClosestDirection(anchorPos);
		if (clickFace == null) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return false;
		}

		// Rotate towards the face.
		Vec3 hitPos = Vec3.atCenterOf(anchorPos).add(clickFace.getStepX() * 0.5, clickFace.getStepY() * 0.5,
				clickFace.getStepZ() * 0.5);

		Aoba.getInstance().rotationManager.setGoal(
				Vec3dGoal.builder().goal(hitPos).mode(rotationMode.getValue()).maxRotation(maxRotation.getValue())
						.pitchRandomness(pitchRandomness.getValue()).yawRandomness(yawRandomness.getValue())
						.fakeRotation(fakeRotation.getValue()).moveFix(moveFix.getValue()).build());

		// Verify glowstone is in the hotbar.
		FindItemResult glowstone = find(Items.GLOWSTONE);
		if (!glowstone.found() || !glowstone.isHotbar()) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return false;
		}

		// Check for crosshair raycast if legit enabled.
		BlockHitResult hit;
		if (legit.getValue()) {
			hit = InteractionUtils.raycastBlock(anchorPos, clickFace);
		} else
			hit = new BlockHitResult(hitPos, clickFace, anchorPos, false);

		// Place block if raycast is valid.
		if (hit == null)
			return false;

		// Swap only after we've confirmed we'll actually click.
		if (autoSwitch.getValue())
			swap(glowstone.slot(), false);

		MC.gameMode.useItemOn(MC.player, InteractionHand.MAIN_HAND, hit);
		MC.player.connection.send(
				new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, 0, MC.player.getYRot(), MC.player.getXRot()));
		MC.player.swing(InteractionHand.MAIN_HAND);
		return true;
	}

	/**
	 * Attempts to find the best position to place a shield.
	 * 
	 * @param anchorPos Position of the anchor to shield.
	 * @return Best placement position of the shield.
	 */
	private BlockPos findShieldPosition(BlockPos anchorPos) {
		Vec3 from = MC.player.getBoundingBox().getCenter();
		Vec3 to = anchorPos.getCenter();
		AABB playerBox = MC.player.getBoundingBox();

		MutableObject<BlockPos> best = new MutableObject<BlockPos>();
		BlockGetter.traverseBlocks(from, to, anchorPos, (anchor, pos) -> {
			if (pos.equals(anchor))
				return Boolean.TRUE;
			if (playerBox.intersects(new AABB(pos)))
				return null;
			if (!hasPlaceableFace(pos))
				return null;
			best.setValue(pos.immutable());
			return null;
		}, _ -> null);

		return best.get();
	}

	/**
	 * Returns whether the block has a place-able face. Skips respawn anchors to
	 * prevent blowing them up by accident.
	 * 
	 * @param pos Position to check.
	 * @return True if the block has a place-able face, false otherwise.
	 */
	private boolean hasPlaceableFace(BlockPos pos) {
		for (Direction d : Direction.values()) {
			BlockPos neighbor = pos.relative(d);
			BlockState neighborState = MC.level.getBlockState(neighbor);

			if (neighborState.is(Blocks.RESPAWN_ANCHOR))
				continue;

			if (neighborState.blocksMotion())
				return true;
		}
		return false;
	}

	/**
	 * Gets whether the player is shielded from an anchor explosion at a position.
	 * 
	 * @param anchorPos Position of the anchor.
	 * @return True if the player has a block between them and the anchor, false
	 *         otherwise.
	 */
	private boolean isShielded(BlockPos anchorPos) {
		Vec3 from = MC.player.getBoundingBox().getCenter();
		Vec3 to = anchorPos.getCenter();
		AABB playerAABB = MC.player.getBoundingBox();

		MutableObject<Boolean> shielded = new MutableObject<Boolean>(Boolean.FALSE);
		BlockGetter.traverseBlocks(from, to, anchorPos, (anchor, pos) -> {
			if (pos.equals(anchor))
				return Boolean.TRUE;

			if (playerAABB.intersects(new AABB(pos)))
				return null;

			if (!MC.level.getBlockState(pos).canBeReplaced()) {
				shielded.setValue(Boolean.TRUE);
				return Boolean.TRUE;
			}
			return null;
		}, _ -> null);

		return shielded.get();
	}

	/**
	 * Attempts to place a shield at a given position.
	 * 
	 * @param shieldPos Position to place the shield.
	 * @return True if the shield was placed, false otherwise.
	 */
	private boolean placeShield(BlockPos shieldPos) {
		// Find ideal placement position and face.
		Direction placementFace = null;
		BlockPos placementPos = null;
		for (Direction d : Direction.values()) {
			BlockPos neighbor = shieldPos.relative(d);
			BlockState neighborState = MC.level.getBlockState(neighbor);
			if (neighborState.is(Blocks.RESPAWN_ANCHOR))
				continue;
			if (!neighborState.blocksMotion())
				continue;
			if (d == Direction.DOWN) {
				placementPos = neighbor;
				placementFace = Direction.UP;
				break;
			}
			if (placementPos == null) {
				placementPos = neighbor;
				placementFace = d.getOpposite();
			}
		}
		if (placementPos == null)
			return false;

		// Rotate towards the position to place.
		Vec3 clickVec = Vec3.atCenterOf(placementPos).add(placementFace.getStepX() * 0.5,
				placementFace.getStepY() * 0.5, placementFace.getStepZ() * 0.5);

		Aoba.getInstance().rotationManager.setGoal(
				Vec3dGoal.builder().goal(clickVec).mode(rotationMode.getValue()).maxRotation(maxRotation.getValue())
						.pitchRandomness(pitchRandomness.getValue()).yawRandomness(yawRandomness.getValue())
						.fakeRotation(fakeRotation.getValue()).moveFix(moveFix.getValue()).build());

		float placeDelayVal = placeDelay.getValue();
		if (placeDelayVal > 0 && System.currentTimeMillis() - lastShieldTime < placeDelayVal)
			return false;

		// Find glowstone in the hotbar.
		FindItemResult glowstone = find(Items.GLOWSTONE);
		if (!glowstone.found() || !glowstone.isHotbar()) {
			Aoba.getInstance().rotationManager.setGoal(null);
			return false;
		}

		// Check for crosshair raycast if legit enabled.
		BlockHitResult hit;
		if (legit.getValue()) {
			hit = InteractionUtils.raycastBlock(placementPos, placementFace);
		} else
			hit = new BlockHitResult(clickVec, placementFace, placementPos, false);

		// Place block if raycast is valid.
		if (hit == null)
			return false;

		// Swap only after we've confirmed we'll actually click.
		if (autoSwitch.getValue())
			swap(glowstone.slot(), false);

		MC.gameMode.useItemOn(MC.player, InteractionHand.MAIN_HAND, hit);
		MC.player.connection.send(
				new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, 0, MC.player.getYRot(), MC.player.getXRot()));
		MC.player.swing(InteractionHand.MAIN_HAND);
		displayedBoxes.put(shieldPos, System.currentTimeMillis());
		return true;
	}

	@Override
	public void onRender(Render3DEvent event) {
		long currentTime = System.currentTimeMillis();
		displayedBoxes.entrySet().removeIf(entry -> currentTime - entry.getValue() >= BOX_DISPLAY_TIME_MS);
		for (Map.Entry<BlockPos, Long> entry : displayedBoxes.entrySet()) {
			event.getRenderer().drawBox(new AABB(entry.getKey()), blockColor.getValue(), 1.0f);
		}
	}
}