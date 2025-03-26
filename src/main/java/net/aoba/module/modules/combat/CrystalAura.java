/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Color;
import net.aoba.managers.rotation.Rotation;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.FindItemResult;
import net.aoba.utils.entity.DamageUtils;
import net.aoba.utils.render.Render3D;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class CrystalAura extends Module implements TickListener, Render3DListener {
	public enum TargetMode {
		NEAREST, MOST_HEALTH
	}

	public enum HandSetting {
		MAIN_HAND, OFF_HAND
	}

	public enum CrystalPriority {
		CLOSEST, HIGHEST_DAMAGE, LOWEST_HEALTH
	}

	private final FloatSetting radius = FloatSetting.builder().id("crystalaura_radius").displayName("Radius")
			.description("Radius, in blocks, that you can place/attack a crystal.").defaultValue(5f).minValue(1f)
			.maxValue(15f).step(1f).build();

	private final FloatSetting placeRadius = FloatSetting.builder().id("crystalaura_place_radius")
			.displayName("Place Radius").description("Radius, in blocks, that you can place/attack a crystal.")
			.defaultValue(5f).minValue(1f).maxValue(15f).step(1f).build();

	private final BooleanSetting targetFriends = BooleanSetting.builder().id("crystalaura_target_friends")
			.displayName("Target Friends").description("Target friends.").defaultValue(false).build();

	private final FloatSetting attackDelay = FloatSetting.builder().id("crystalaura_attack_delay")
			.displayName("Attack Delay").description("Delay between attacks in milliseconds.").defaultValue(500f)
			.minValue(0f).maxValue(2000f).step(50f).build();

	private final BooleanSetting autoSwitch = BooleanSetting.builder().id("crystalaura_auto_switch")
			.displayName("Auto Switch").description("Automatically switch to End Crystal.").defaultValue(true).build();

	private final FloatSetting placeDelay = FloatSetting.builder().id("crystalaura_place_delay")
			.displayName("Place Delay").description("Delay between placing crystals in milliseconds.")
			.defaultValue(500f).minValue(0f).maxValue(2000f).step(50f).build();

	private final BooleanSetting multiPlace = BooleanSetting.builder().id("crystalaura_multi_place")
			.displayName("MultiPlace").description("Allows placing multiple crystals simultaneously.")
			.defaultValue(false).build();

	private final BooleanSetting antiSuicide = BooleanSetting.builder().id("crystalaura_anti_suicide")
			.displayName("AntiSuicide").description("Prevents attacking crystals if it would result in player's death.")
			.defaultValue(true).build();

	private final FloatSetting minDamage = FloatSetting.builder().id("crystalaura_min_damage").displayName("Min Damage")
			.description("Minimum damage a crystal must deal to be placed or attacked.").defaultValue(6f).minValue(0f)
			.maxValue(36f).step(0.5f).build();

	private final BooleanSetting swingHand = BooleanSetting.builder().id("crystalaura_swing_hand")
			.displayName("Swing Hand").description("Swing hand after interacting.").defaultValue(true).build();

	private final BooleanSetting ignoreWalls = BooleanSetting.builder().id("crystalaura_ignore_walls")
			.displayName("Ignore Walls").description("Ignore walls when targeting enemies.").defaultValue(true).build();

	private final EnumSetting<TargetMode> targetMode = EnumSetting.<TargetMode>builder().id("crystalaura_target_mode")
			.displayName("Target Mode").description("Mode to target players.").defaultValue(TargetMode.NEAREST).build();

	private final EnumSetting<CrystalPriority> crystalPriority = EnumSetting.<CrystalPriority>builder()
			.id("crystalaura_crystal_priority").displayName("Crystal Priority")
			.description("Prioritize which crystals to attack first.").defaultValue(CrystalPriority.CLOSEST).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("crystalaura_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private final EnumSetting<HandSetting> handSetting = EnumSetting.<HandSetting>builder()
			.id("crystalaura_hand_setting").displayName("Hand Setting").description("The hand to use for interactions.")
			.defaultValue(HandSetting.MAIN_HAND).build();

	private final FloatSetting maxSelfDamage = FloatSetting.builder().id("crystalaura_max_self_damage")
			.displayName("Max Self Damage")
			.description("Maximum self-damage the player can take from a single crystal.").defaultValue(4f).minValue(0f)
			.maxValue(20f).step(0.5f).build();

	private final FloatSetting enemyRange = FloatSetting.builder().id("crystalaura_enemy_range")
			.displayName("Enemy Range").description("Maximum distance an enemy can be to be considered a target.")
			.defaultValue(12f).minValue(0f).maxValue(32f).step(1f).build();

	private final FloatSetting wallRange = FloatSetting.builder().id("crystalaura_wall_range").displayName("Wall Range")
			.description("Distance an enemy must be to a wall for crystals to be placed or attacked through it.")
			.defaultValue(3f).minValue(0f).maxValue(8f).step(0.5f).build();

	private final ColorSetting color = ColorSetting.builder().id("crystalaura_color").displayName("Color")
			.description("Color").defaultValue(new Color(0, 1f, 1f)).build();

	private long lastAttackTime;
	private long lastPlaceTime;
	private BlockPos placePos;
	private final long BOX_DISPLAY_TIME_MS = 3000;
	private final Map<BlockPos, Long> displayedBoxes = new HashMap<>();

	public CrystalAura() {
		super("CrystalAura");

		setCategory(Category.of("Combat"));
		setDescription("Attacks anything within your personal space with a End Crystal.");

		addSettings(ignoreWalls);
		addSetting(swingHand);
		addSetting(handSetting);
		addSetting(radius);
		addSetting(placeRadius);
		addSetting(targetFriends);
		addSetting(attackDelay);
		addSetting(autoSwitch);
		addSetting(placeDelay);
		addSetting(targetMode);
		addSetting(multiPlace);
		addSetting(antiSuicide);
		addSetting(minDamage);
		addSetting(maxSelfDamage);
		addSetting(enemyRange);
		addSetting(wallRange);
		addSetting(crystalPriority);
		addSetting(rotationMode);
		addSetting(color);

		lastAttackTime = 0;
		lastPlaceTime = 0;
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
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

		if (currentTime - lastPlaceTime >= placeDelay.getValue()) {
			placeCrystal();
			lastPlaceTime = currentTime;
		}

		if (currentTime - lastAttackTime >= attackDelay.getValue()) {
			attackCrystal();
			lastAttackTime = currentTime;
		}
	}

	@Override
	public void onTick(TickEvent.Post event) {

	}

	private void placeCrystal() {
		Hand hand = handSetting.getValue() == HandSetting.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND;

		FindItemResult result = find(Items.END_CRYSTAL);

		if (!result.found() && !result.isHotbar())
			return;

		for (PlayerEntity player : Aoba.getInstance().entityManager.getPlayers()) {
			if (player == MC.player || MC.player.squaredDistanceTo(player) > radius.getValueSqr())
				continue;
			if (!targetFriends.getValue() && Aoba.getInstance().friendsList.contains(player))
				continue;

			Optional<BlockPos> bestPos = findBestCrystalPlacement(player);
			if (bestPos.isPresent()) {
				placePos = bestPos.get();

				double damage = DamageUtils.crystalDamage(player, Vec3d.of(placePos.add(0, 1, 0)));
				if (damage < minDamage.getValue())
					continue;

				if (autoSwitch.getValue()) {
					swap(result.slot(), false);
				}

				if (multiPlace.getValue()) {
					performMultiPlace(placePos);
				} else {
					BlockHitResult hitResult = new BlockHitResult(placePos.toCenterPos(), Direction.UP, placePos,
							false);
					MC.interactionManager.interactBlock(MC.player, hand, hitResult);
					MC.player.networkHandler.sendPacket(
							new PlayerInteractItemC2SPacket(hand, 0, MC.player.getYaw(), MC.player.getPitch()));
				}

				displayedBoxes.put(placePos, System.currentTimeMillis()); // Store rendering time
				lastPlaceTime = System.currentTimeMillis();
				break;
			}
		}
	}

	private void performMultiPlace(BlockPos initialPos) {
		Hand hand = handSetting.getValue() == HandSetting.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND;

		BlockPos[] positions = { initialPos, initialPos.up(), initialPos.east(), initialPos.west(), initialPos.north(),
				initialPos.south() };

		for (BlockPos pos : positions) {
			BlockHitResult hitResult = new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, false);
			MC.interactionManager.interactBlock(MC.player, hand, hitResult);
			MC.player.networkHandler
					.sendPacket(new PlayerInteractItemC2SPacket(hand, 0, MC.player.getYaw(), MC.player.getPitch()));
		}
	}

	private Optional<BlockPos> findBestCrystalPlacement(PlayerEntity player) {
		BlockPos playerPos = player.getBlockPos();
		double maxDamage = 0;
		double minDistance = Double.MAX_VALUE;
		BlockPos bestPos = null;

		int radius = placeRadius.getValue().intValue();
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos pos = playerPos.add(x, y, z);
					BlockState blockState = MC.world.getBlockState(pos);
					Block block = blockState.getBlock();
					if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK)
						continue;

					// Ensure the block above is air
					BlockPos abovePos = pos.up();
					BlockState aboveBlockState = MC.world.getBlockState(abovePos);
					if (!aboveBlockState.isAir())
						continue;

					// Ensure there is air two blocks above the potential placement
					BlockPos abovePos2 = pos.up(2);
					BlockState aboveBlockState2 = MC.world.getBlockState(abovePos2);
					if (!aboveBlockState2.isAir())
						continue;

					if (pos.getY() > playerPos.getY() + 1)
						continue;

					double damage = DamageUtils.crystalDamage(player, Vec3d.of(pos));
					double distance = playerPos.getSquaredDistance(pos);

					if (pos.getY() == playerPos.getY()) {
						damage *= 1.5;
					}

					if (damage > maxDamage || (damage == maxDamage && distance < minDistance)) {
						maxDamage = damage;
						minDistance = distance;
						bestPos = pos;
					}
				}
			}
		}

		return Optional.ofNullable(bestPos);
	}

	private PlayerEntity getTargetPlayer() {
		List<AbstractClientPlayerEntity> players = MC.world.getPlayers();
		PlayerEntity targetPlayer = null;
		double targetValue = (targetMode.getValue() == TargetMode.NEAREST) ? Double.MAX_VALUE : 0;

		for (PlayerEntity player : players) {
			if (player == MC.player || !targetFriends.getValue() && Aoba.getInstance().friendsList.contains(player))
				continue;
			double distance = MC.player.squaredDistanceTo(player);
			double health = player.getHealth();

			if (targetMode.getValue() == TargetMode.NEAREST && distance < targetValue) {
				targetValue = distance;
				targetPlayer = player;
			} else if (targetMode.getValue() == TargetMode.MOST_HEALTH && health > targetValue) {
				targetValue = health;
				targetPlayer = player;
			}
		}

		return targetPlayer;
	}

	private void attackCrystal() {
		PlayerEntity targetPlayer = getTargetPlayer();

		Hand hand = handSetting.getValue() == HandSetting.MAIN_HAND ? Hand.MAIN_HAND : Hand.OFF_HAND;

		if (targetPlayer == null)
			return;

		EndCrystalEntity bestCrystal = null;
		double maxDamage = 0;

		double maxSelfDamageAllowed = maxSelfDamage.getValue();
		double enemyRangeSquared = enemyRange.getValue() * enemyRange.getValue();
		double wallRangeSquared = wallRange.getValue() * wallRange.getValue();

		Iterable<Entity> entities = Aoba.getInstance().entityManager.getEntities();
		for (Entity entity : entities) {
			if (entity instanceof EndCrystalEntity) {
				double distanceSquared = MC.player.squaredDistanceTo(entity);
				if (distanceSquared < enemyRangeSquared) {
					double damage = DamageUtils.crystalDamage(targetPlayer, entity.getPos());
					double selfDamage = DamageUtils.crystalDamage(MC.player, entity.getPos());

					if (damage < minDamage.getValue()
							|| (antiSuicide.getValue() && selfDamage > maxSelfDamageAllowed)) {
						continue;
					}

					boolean isBehindWall = distanceSquared > wallRangeSquared && isCrystalBehindWall(entity);

					if (!ignoreWalls.getValue() && isBehindWall) {
						continue;
					}

					switch (crystalPriority.getValue()) {
					case CLOSEST:
						if (bestCrystal == null || distanceSquared < MC.player.squaredDistanceTo(bestCrystal)) {
							bestCrystal = (EndCrystalEntity) entity;
							maxDamage = damage;
						}
						break;
					case HIGHEST_DAMAGE:
						if (damage > maxDamage) {
							bestCrystal = (EndCrystalEntity) entity;
							maxDamage = damage;
						}
						break;
					case LOWEST_HEALTH:
						double targetHealth = targetPlayer.getHealth();
						if (targetHealth > 0 && targetHealth < MC.player.getHealth()) {
							bestCrystal = (EndCrystalEntity) entity;
							maxDamage = damage;
						}
						break;
					default:
						break;
					}
				}
			}
		}

		if (bestCrystal != null) {
			Vec3d targetPos = bestCrystal.getPos().add(0, bestCrystal.getBoundingBox().getLengthY() / 2.0, 0);

			switch (rotationMode.getValue()) {
			case NONE:
				MC.player.networkHandler
						.sendPacket(PlayerInteractEntityC2SPacket.attack(bestCrystal, MC.player.isSneaking()));
				if (swingHand.getValue())
					MC.player.swingHand(hand);
				break;
			case INSTANT:
				MC.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, targetPos);
				MC.player.networkHandler
						.sendPacket(PlayerInteractEntityC2SPacket.attack(bestCrystal, MC.player.isSneaking()));
				break;
			case SMOOTH:
				// Instant rotation for now because im too dumb to figure out smooth rotation
				float rotationDegreesPerTick = 10f;
				Rotation rotation = Rotation.getPlayerRotationDeltaFromEntity(bestCrystal);

				float maxYawRotationDelta = Math.clamp((float) -rotation.yaw(), -rotationDegreesPerTick,
						rotationDegreesPerTick);
				float maxPitchRotation = Math.clamp((float) -rotation.pitch(), -rotationDegreesPerTick,
						rotationDegreesPerTick);

				Rotation newRotation = new Rotation(MC.player.getYaw() + maxYawRotationDelta,
						MC.player.getPitch() + maxPitchRotation);
				MC.player.setYaw((float) newRotation.yaw());
				MC.player.setPitch((float) newRotation.pitch());

				MC.player.networkHandler
						.sendPacket(PlayerInteractEntityC2SPacket.attack(bestCrystal, MC.player.isSneaking()));
				if (swingHand.getValue())
					MC.player.swingHand(hand);

				break;
			default:
				break;
			}
		}
	}

	private boolean isCrystalBehindWall(Entity crystal) {
		BlockPos crystalPos = crystal.getBlockPos();
		Vec3d playerEyePos = MC.player.getCameraPosVec(1.0f);

		Vec3d toCrystalVec = crystal.getPos().subtract(playerEyePos);

		Vec3d extendedVec = playerEyePos.add(toCrystalVec.multiply(1.1));
		VoxelShape crystalShape = VoxelShapes.cuboid(crystal.getBoundingBox());

		BlockHitResult result = MC.world.raycastBlock(playerEyePos, extendedVec, crystalPos, crystalShape,
				crystal.getBlockStateAtPos());

		if (result != null && result.getType() == BlockHitResult.Type.BLOCK) {
			BlockPos blockPos = result.getBlockPos();
			BlockState blockState = MC.world.getBlockState(blockPos);
			Block block = blockState.getBlock();

			return block != Blocks.AIR && blockState.isSolidBlock(MC.world, blockPos);
		}

		return false;
	}

	@Override
	public void onRender(Render3DEvent event) {
		long currentTime = System.currentTimeMillis();

		displayedBoxes.entrySet().removeIf(entry -> {
			long renderTime = entry.getValue();
            return currentTime - renderTime >= BOX_DISPLAY_TIME_MS;
        });

		for (Map.Entry<BlockPos, Long> entry : displayedBoxes.entrySet()) {
			BlockPos pos = entry.getKey();
			Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), new Box(pos), color.getValue(), 1.0f);
		}
	}
}
