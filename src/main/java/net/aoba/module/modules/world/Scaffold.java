/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.world;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.managers.rotation.RotationMode;
import net.aoba.managers.rotation.goals.Vec3dGoal;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.player.InteractionUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Scaffold extends Module implements TickListener {

	private record ScaffoldPlaceResult(BlockPos pos, Direction direction) {
	}

	private final FloatSetting radius = FloatSetting.builder().id("scaffold_radius").displayName("Radius")
			.description("How far Scaffold will place a block below the player.").defaultValue(4f).minValue(1f)
			.maxValue(10f).step(0.5f).build();

	private final FloatSetting placeDelay = FloatSetting.builder().id("scaffold_place_delay").displayName("Place Delay")
			.description("How long (in ticks) until Scaffold will place the next block below the player.")
			.defaultValue(0f).minValue(0f).maxValue(20f).step(1f).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("scaffold_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private final FloatSetting maxRotation = FloatSetting.builder().id("scaffold_max_rotation")
			.displayName("Max Rotation").description("The max speed that Aimbot will rotate").defaultValue(10.0f)
			.minValue(1.0f).maxValue(360.0f).build();

	private final FloatSetting yawRandomness = FloatSetting.builder().id("scaffold_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final FloatSetting pitchRandomness = FloatSetting.builder().id("scaffold_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final BooleanSetting fakeRotation = BooleanSetting.builder().id("scaffold_fake_rotation")
			.displayName("Fake Rotation")
			.description("Spoofs the client's rotation so that the player appears rotated on the server")
			.defaultValue(false).build();

	private final BooleanSetting swingHand = BooleanSetting.builder().id("scaffold_swing_hand")
			.displayName("Swing Hand").description("Swing hand when placing blocks.").defaultValue(true).build();

	private int yPosition = 0;
	private int curDelay = 0;

	public Scaffold() {
		super("Scaffold");
		setCategory(Category.of("World"));
		setDescription("Automatically places blocks below the player.");

		addSetting(radius);
		addSetting(placeDelay);
		addSetting(rotationMode);
		addSetting(maxRotation);
		addSetting(yawRandomness);
		addSetting(pitchRandomness);
		addSetting(fakeRotation);
		addSetting(swingHand);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Aoba.getInstance().rotationManager.setGoal(null);
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
		PlayerInventory inventory = MC.player.getInventory();
		ItemStack currentHand = inventory.getSelectedStack();
		if (currentHand.getItem() instanceof BlockItem) {
			ScaffoldPlaceResult placementPos = findBlockPosToPlace();

			if (curDelay >= placeDelay.getValue() && placementPos != null) {
				Vec3d placementPosVec = placementPos.pos.toCenterPos();

				Vec3dGoal rotation = Vec3dGoal.builder().goal(placementPosVec).mode(rotationMode.getValue())
						.maxRotation(maxRotation.getValue()).pitchRandomness(pitchRandomness.getValue())
						.yawRandomness(yawRandomness.getValue()).fakeRotation(fakeRotation.getValue()).build();
				Aoba.getInstance().rotationManager.setGoal(rotation);

				InteractionUtils.placeBlock(placementPos.pos, Hand.MAIN_HAND, true);
				curDelay = 0;
			} else {
				Aoba.getInstance().rotationManager.setGoal(null);
				curDelay++;
			}
		}
	}

	@Override
	public void onTick(Post event) {
		// Determine the height at which the player will build.
		if (MC.player.isOnGround()) {
			if (MC.options.jumpKey.isPressed()) {
				yPosition = MC.player.getBlockPos().getY();
			} else
				yPosition = MC.player.getBlockPos().getY() - 1;
		}
	}

	private ScaffoldPlaceResult findBlockPosToPlace() {
		BlockPos playerPos = MC.player.getBlockPos();
		BlockPos underneathPosition = new BlockPos(playerPos.getX(), yPosition, playerPos.getZ());

		if (!MC.world.isAir(underneathPosition))
			return null;

		BlockPos result = null;
		Direction resultDirection = null;
		double lastDistanceTo = Float.MAX_VALUE;

		int radiusInt = radius.getValue().intValue();
		for (int x = -radiusInt; x < radiusInt; x++) {
			for (int z = -radiusInt; z < radiusInt; z++) {
				BlockPos checkPos = underneathPosition.add(x, 0, z);
				Direction directionToPlace = findAdjacentBlockFace(checkPos);

				if (directionToPlace != null) {
					double distanceToBlock = MC.player.squaredDistanceTo(checkPos.toCenterPos());

					if (result == null || distanceToBlock < lastDistanceTo) {
						result = checkPos;
						resultDirection = directionToPlace;
						lastDistanceTo = distanceToBlock;
					}
				}
			}
		}
		if (result == null || resultDirection == null
				|| result.getSquaredDistance(MC.player.getPos()) > radius.getValueSqr())
			return null;
		else
			return new ScaffoldPlaceResult(result, resultDirection);
	}

	private Direction findAdjacentBlockFace(BlockPos pos) {
		BlockPos north = pos.north();
		BlockPos south = pos.south();
		BlockPos west = pos.west();
		BlockPos east = pos.east();
		BlockPos up = pos.up();
		BlockPos down = pos.down();

		if (!MC.world.isAir(north) && !MC.world.getFluidState(north).isOf(Fluids.LAVA)
				&& !MC.world.getFluidState(north).isOf(Fluids.WATER)) {
			return Direction.SOUTH;
		} else if (!MC.world.isAir(east) && !MC.world.getFluidState(east).isOf(Fluids.LAVA)
				&& !MC.world.getFluidState(east).isOf(Fluids.WATER)) {
			return Direction.WEST;
		} else if (!MC.world.isAir(south) && !MC.world.getFluidState(south).isOf(Fluids.LAVA)
				&& !MC.world.getFluidState(south).isOf(Fluids.WATER)) {
			return Direction.NORTH;
		} else if (!MC.world.isAir(west) && !MC.world.getFluidState(west).isOf(Fluids.LAVA)
				&& !MC.world.getFluidState(west).isOf(Fluids.WATER)) {
			return Direction.EAST;
		} else if (!MC.world.isAir(up) && !MC.world.getFluidState(up).isOf(Fluids.LAVA)
				&& !MC.world.getFluidState(up).isOf(Fluids.WATER)) {
			return Direction.UP;
		} else if (!MC.world.isAir(down) && !MC.world.getFluidState(down).isOf(Fluids.LAVA)
				&& !MC.world.getFluidState(down).isOf(Fluids.WATER)) {
			return Direction.NORTH;
		} else
			return null;
	}
}