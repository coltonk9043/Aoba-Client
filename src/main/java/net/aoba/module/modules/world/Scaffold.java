package net.aoba.module.modules.world;

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent.Post;
import net.aoba.event.events.TickEvent.Pre;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.rotation.Rotation;
import net.aoba.utils.rotation.RotationMode;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Scaffold extends Module implements TickListener {

	private record ScaffoldPlaceResult(BlockPos pos, Direction direction) {
	}

	private final FloatSetting radius = FloatSetting.builder().id("scaffold_radius").displayName("Radius")
			.description("How far Scaffold will place a block below the player.").defaultValue(5f).minValue(1f)
			.maxValue(10f).step(1f).build();

	private final FloatSetting placeDelay = FloatSetting.builder().id("scaffold_place_delay").displayName("Place Delay")
			.description("How long (in ticks) until Scaffold will place the next block below the player.")
			.defaultValue(0f).minValue(1f).maxValue(20f).step(1f).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("scaffold_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates when a block is to be placed.")
			.defaultValue(RotationMode.NONE).build();

	private final BooleanSetting swingHand = BooleanSetting.builder().id("scaffold_swing_hand")
			.displayName("Swing Hand").description("Swing hand when placing blocks.").defaultValue(true).build();

	private int yPosition = 0;
	private int curDelay = 0;

	public Scaffold() {
		super("Scaffold");
		this.setCategory(Category.of("World"));
		this.setDescription("Automatically places blocks below the player.");

		this.addSetting(radius);
		this.addSetting(placeDelay);
		this.addSetting(rotationMode);
		this.addSetting(swingHand);
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
		ScaffoldPlaceResult placementPos = findBlockPosToPlace();

		if (curDelay >= placeDelay.getValue()) {
			if (placementPos != null) {
				Vec3d placementPosVec = placementPos.pos.toCenterPos();

				switch (rotationMode.getValue()) {
				case NONE:
					break;
				case INSTANT:
					MC.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, placementPosVec);
					break;
				case SMOOTH:
					// Instant rotation for now because im too dumb to figure out smooth rotation
					float rotationDegreesPerTick = 10f;
					Rotation rotation = Rotation.getPlayerRotationDeltaFromPosition(placementPosVec);

					float maxYawRotationDelta = Math.clamp((float) -rotation.yaw(), -rotationDegreesPerTick,
							rotationDegreesPerTick);
					float maxPitchRotation = Math.clamp((float) -rotation.pitch(), -rotationDegreesPerTick,
							rotationDegreesPerTick);

					Rotation newRotation = new Rotation(MC.player.getYaw() + maxYawRotationDelta,
							MC.player.getPitch() + maxPitchRotation);
					MC.player.setYaw((float) newRotation.yaw());
					MC.player.setPitch((float) newRotation.pitch());
					break;
				default:
					break;
				}

				BlockHitResult rayTrace = new BlockHitResult(MC.player.getPos(), placementPos.direction,
						placementPos.pos, false);
				ActionResult result = MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, rayTrace);

				if (result.isAccepted()) {
					if (swingHand.getValue())
						MC.player.swingHand(Hand.MAIN_HAND);
				} else
					MC.interactionManager.interactItem(MC.player, Hand.MAIN_HAND);

				curDelay = 0;
			} else
				curDelay++;
		} else
			curDelay++;
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