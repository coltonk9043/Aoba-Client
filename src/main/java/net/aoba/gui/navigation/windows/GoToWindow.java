/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.windows;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.math.NumberUtils;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.components.ButtonComponent;
import net.aoba.gui.components.CheckboxComponent;
import net.aoba.gui.components.EnumComponent;
import net.aoba.gui.components.SeparatorComponent;
import net.aoba.gui.components.SliderComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.components.TextBoxComponent;
import net.aoba.gui.navigation.Window;
import net.aoba.managers.SettingManager;
import net.aoba.managers.pathfinding.AbstractPathManager;
import net.aoba.managers.pathfinding.FlyPathManager;
import net.aoba.managers.pathfinding.PathNode;
import net.aoba.managers.pathfinding.TeleportPathManager;
import net.aoba.managers.pathfinding.WalkingPathManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.StringSetting;
import net.aoba.utils.render.Render3D;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class GoToWindow extends Window implements TickListener, Render3DListener {

	public enum Pathfinder {
		Walk, Fly, Teleport,
	}

	private static final MinecraftClient MC = MinecraftClient.getInstance();
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();

	private final ButtonComponent startButton;
	private final StringComponent startButtonText;
	private final ButtonComponent setPositionButton;
	private SliderComponent radiusSlider;
	private SliderComponent flyMaxSpeedSlider;

	private final EnumSetting<Pathfinder> pathfinderMode;
	private final BooleanSetting avoidWater;
	private final BooleanSetting avoidLava;
	private final FloatSetting radius;
	private final StringSetting locationX;
	private final StringSetting locationY;
	private final StringSetting locationZ;
	private final FloatSetting maxSpeed;

	private final Runnable startRunnable;
	private final Runnable clearRunnable;
	private final Runnable setPositionRunnable;
	private BlockPos start;
	private BlockPos end;
	private BlockPos actualEnd;
	private AbstractPathManager pathManager;

	private int currentNodeIndex = 0;
	private ArrayList<PathNode> nodes;
	private boolean isStarted = false;

	public GoToWindow() {
		super("Go To Location", 540, 150);

		minWidth = 350f;

		pathfinderMode = EnumSetting.<Pathfinder>builder().id("goto_pathfinder_mode").displayName("Mode")
				.defaultValue(Pathfinder.Walk).onUpdate(var -> {
					switch (var) {
					case Fly:
						pathManager = new FlyPathManager();
						break;
					case Walk:
						pathManager = new WalkingPathManager();
						break;
					case Teleport:
						pathManager = new TeleportPathManager();
						break;
					}

					// Disable Radius setting if not teleport
					radiusSlider.setVisible(var == Pathfinder.Teleport);
					flyMaxSpeedSlider.setVisible(var == Pathfinder.Fly);

					if (isStarted)
						recalculatePathAsync();
				}).build();

		avoidWater = BooleanSetting.builder().id("goto_avoid_water").displayName("Avoid Water")
				.description("Whether the pathfinder will avoid Water").defaultValue(false).onUpdate(var -> {
					pathManager.setAvoidWater(var);
					if (isStarted)
						recalculatePathAsync();
				}).build();

		avoidLava = BooleanSetting.builder().id("goto_avoid_lava").displayName("Avoid Lava")
				.description("Whether the pathfinder will avoid Lava").defaultValue(false).onUpdate(var -> {
					pathManager.setAvoidLava(var);
					if (isStarted)
						recalculatePathAsync();
				}).build();

		radius = FloatSetting.builder().id("goto_radius").displayName("Teleport Radius")
				.description("The radius that the teleport pathfinder will attempt to find a block within.")
				.defaultValue(5.0f).minValue(1.0f).maxValue(100.0f).step(1.0f).onUpdate(s -> {
					if (pathManager instanceof TeleportPathManager tpManager) {
						tpManager.setRadius(s);
						if (isStarted)
							recalculatePathAsync();
					}
				}).build();

		locationX = StringSetting.builder().id("goto_location_x").displayName("X Coord").description("X Coordinate")
				.build();

		locationY = StringSetting.builder().id("goto_location_y").displayName("Y Coord").description("Y Coordinate")
				.build();

		locationZ = StringSetting.builder().id("goto_location_z").displayName("Z Coord").description("Z Coordinate")
				.build();

		maxSpeed = FloatSetting.builder().id("goto_max_speed").displayName("Max Speed").description("Max Speed")
				.defaultValue(4.0f).minValue(0.5f).maxValue(15.0f).step(0.5f).build();

		// Register Settings
		SettingManager.registerSetting(pathfinderMode);
		SettingManager.registerSetting(avoidWater);
		SettingManager.registerSetting(avoidLava);
		SettingManager.registerSetting(radius);
		SettingManager.registerSetting(locationX);
		SettingManager.registerSetting(locationY);
		SettingManager.registerSetting(locationZ);
		SettingManager.registerSetting(maxSpeed);

		StackPanelComponent stackPanel = new StackPanelComponent();

		stackPanel.addChild(new StringComponent("PathFinding"));
		stackPanel.addChild(new SeparatorComponent());

		StringComponent label = new StringComponent(
				"GoTo will automatically walk/fly your player to specific coordinates.");
		stackPanel.addChild(label);

		EnumComponent<Pathfinder> pathfinderModeComponent = new EnumComponent<Pathfinder>(pathfinderMode);
		stackPanel.addChild(pathfinderModeComponent);

		CheckboxComponent avoidWaterCheckbox = new CheckboxComponent(avoidWater);
		stackPanel.addChild(avoidWaterCheckbox);

		CheckboxComponent avoidLavaCheckbox = new CheckboxComponent(avoidLava);
		stackPanel.addChild(avoidLavaCheckbox);

		radiusSlider = new SliderComponent(radius);
		stackPanel.addChild(radiusSlider);

		flyMaxSpeedSlider = new SliderComponent(maxSpeed);
		stackPanel.addChild(flyMaxSpeedSlider);

		stackPanel.addChild(new StringComponent("X Coordinate"));
		TextBoxComponent locationXTextBox = new TextBoxComponent(locationX);
		stackPanel.addChild(locationXTextBox);

		stackPanel.addChild(new StringComponent("Y Coordinate"));
		TextBoxComponent locationYTextBox = new TextBoxComponent(locationY);
		stackPanel.addChild(locationYTextBox);

		stackPanel.addChild(new StringComponent("Z Coordinate"));
		TextBoxComponent locationZTextBox = new TextBoxComponent(locationZ);
		stackPanel.addChild(locationZTextBox);

		startRunnable = new Runnable() {
			@Override
			public void run() {
				startButtonText.setText("Cancel");
				startButton.setOnClick(clearRunnable);
				isStarted = true;
				recalculatePathAsync();
				registerEvents();
			}
		};

		clearRunnable = new Runnable() {
			@Override
			public void run() {
				unregisterEvents();
				clear();
				startButtonText.setText("Calculate");
				startButton.setOnClick(startRunnable);
			}
		};

		setPositionRunnable = new Runnable() {
			@Override
			public void run() {
				MinecraftClient MC = MinecraftClient.getInstance();
				BlockPos pos = MC.player.getBlockPos();
				locationX.setValue(String.valueOf(pos.getX()));
				locationY.setValue(String.valueOf(pos.getY()));
				locationZ.setValue(String.valueOf(pos.getZ()));
			}
		};

		startButton = new ButtonComponent(startRunnable);
		startButtonText = new StringComponent("Calculate");
		startButton.addChild(startButtonText);
		stackPanel.addChild(startButton);

		setPositionButton = new ButtonComponent(setPositionRunnable);
		setPositionButton.addChild(new StringComponent("Set Position"));
		stackPanel.addChild(setPositionButton);

		addChild(stackPanel);

		pathManager = new WalkingPathManager();
	}

	private void recalculatePathAsync() {
		executor.submit(this::recalculatePath);
	}

	private void registerEvents() {

		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	private void unregisterEvents() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	private void recalculatePath() {
		BlockPos targetPos = getNextTarget();

		int x = Integer.parseInt(locationX.getValue());
		int y = Integer.parseInt(locationY.getValue());
		int z = Integer.parseInt(locationZ.getValue());

		pathManager.setTarget(targetPos);
		nodes = pathManager.recalculatePath(MC.player.getBlockPos());

		start = MC.player.getBlockPos();
		end = targetPos;
		actualEnd = new BlockPos(x, y, z);
		currentNodeIndex = 0;
	}

	private BlockPos getNextTarget() {
		if (!NumberUtils.isParsable(locationX.getValue()) || !NumberUtils.isParsable(locationY.getValue())
				|| !NumberUtils.isParsable(locationZ.getValue()))
			return null;

		int x = Integer.parseInt(locationX.getValue());
		int y = Integer.parseInt(locationY.getValue());
		int z = Integer.parseInt(locationZ.getValue());
		Vec3d target = new Vec3d(x, y, z);

		BlockPos targetPos = new BlockPos(x, y, z);

		ChunkPos targetChunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(targetPos.getX()),
				ChunkSectionPos.getSectionCoord(targetPos.getZ()));

		// Find the new chunk / position that we want to try to get to, hoping that we
		// can find one.
		BlockPos playerPos = MC.player.getBlockPos();
		Vec3d delta = target.subtract(playerPos.toCenterPos()).normalize();
		Vec3d offset = new Vec3d(delta.x, delta.y, delta.z);
		BlockPos newTarget = playerPos;
		BlockPos temp = playerPos;
		boolean foundTargetInLoadedChunks = false;

		ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(temp.getX()),
				ChunkSectionPos.getSectionCoord(temp.getZ()));

		// HashSet<Chunk> visittedChunks = new HashSet<Chunk>();
		while (MC.world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z)) {
			newTarget = temp;
			temp = playerPos.add((int) Math.ceil(offset.x), (int) Math.ceil(offset.y), (int) Math.ceil(offset.z));
			offset = offset.add(delta);
			chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(temp.getX()),
					ChunkSectionPos.getSectionCoord(temp.getZ()));

			// visittedChunks.add(chunk);
			if (chunkPos.equals(targetChunkPos)) {
				newTarget = targetPos;
				foundTargetInLoadedChunks = true;
				break;
			}
		}

		// We want to ensure that if we could NOT find the loaded chunk, that we move to
		// the highest point in the middle of that chunk.
		// If it doesn't, it is possible that the target would be inside of the bllock.
		if (!foundTargetInLoadedChunks)
			newTarget = getHighestBlock(newTarget);

		return newTarget;
	}

	private BlockPos getHighestBlock(BlockPos current) {
		boolean isTeleportMode = pathfinderMode.getValue() == Pathfinder.Teleport;
		BlockPos prevPos = null;
		for (int i = 320; i >= -64; i--) {
			BlockPos pos = current.withY(i);
			BlockState state = MC.world.getBlockState(pos);

			if ((isTeleportMode && !state.isAir() && !state.getFluidState().isIn(FluidTags.WATER)
					&& !state.getFluidState().isIn(FluidTags.LAVA) && !(state.getBlock() instanceof PlantBlock))
					|| (!isTeleportMode && !state.isAir()))
				break;

			prevPos = pos;
		}

		if (pathfinderMode.getValue() == Pathfinder.Fly) {
			if (prevPos.getY() >= current.getY())
				return prevPos;
			else
				return current;
		} else
			return prevPos;
	}

	private void clear() {
		nodes = null;
		isStarted = false;
	}

	@Override
	public void onRender(Render3DEvent event) {
		if (nodes != null) {
			Box startBox = new Box(start);
			Box endBox = new Box(end);

			Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), startBox, Colors.Red, 1.0f);
			Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), endBox, Colors.Red, 1.0f);

			for (int i = 0; i < nodes.size() - 1; i++) {
				PathNode first = nodes.get(i);
				PathNode second = nodes.get(i + 1);

				Vec3d pos1 = first.pos.toCenterPos().add(-0.15f, -0.15f, -0.15f);
				Vec3d pos2 = first.pos.toCenterPos().add(0.15f, 0.15f, 0.15f);
				Box box = new Box(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);

				Render3D.draw3DBox(event.GetMatrix(), event.getCamera(), box, Colors.Red, 1);
				Render3D.drawLine3D(event.GetMatrix(), event.getCamera(), first.pos.toCenterPos(),
						second.pos.toCenterPos(), Colors.Red);
			}
		}
	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
		MinecraftClient MC = MinecraftClient.getInstance();
		if (nodes == null)
			return;

		if (currentNodeIndex < nodes.size() - 1) {
			// Check next position
			PathNode next = nodes.get(currentNodeIndex + 1);
			BlockPos playerPos;

			if (MC.player.isRiding()) {
				Entity riding = MC.player.getRootVehicle();
				playerPos = riding.getBlockPos();
			} else
				playerPos = MC.player.getBlockPos();

			if (playerPos.equals(next.pos)) {
				currentNodeIndex++;
				if (currentNodeIndex < nodes.size() - 1)
					next = nodes.get(currentNodeIndex + 1);
				else
					return;
			}

			Vec3d nextCenterPos = next.pos.toBottomCenterPos();

			switch (pathfinderMode.getValue()) {
			case Fly:
				double velocity = Math.min(maxSpeed.getValue(), MC.player.getPos().distanceTo(nextCenterPos));
				Vec3d direction = nextCenterPos.subtract(MC.player.getPos()).normalize().multiply(velocity);

				// Check to see if the player is in a vehicle. If they are, we want to apply
				// velocity to the vehicle (boatfly)
				if (MC.player.isRiding()) {
					Entity riding = MC.player.getRootVehicle();
					riding.lookAt(EntityAnchor.EYES, new Vec3d(nextCenterPos.x, MC.player.getEyeY(), nextCenterPos.z));
					riding.setVelocity(direction);
				} else
					MC.player.setVelocity(direction);
				break;
			case Walk:
				MC.player.getAbilities().flying = false;
				MC.player.lookAt(EntityAnchor.EYES, new Vec3d(nextCenterPos.x, MC.player.getEyeY(), nextCenterPos.z));
				MC.options.forwardKey.setPressed(true);
				MC.options.jumpKey.setPressed(next.getIsInWater() || next.getIsInLava() || next.getWasJump()
						|| MC.player.horizontalCollision);
				break;
			case Teleport:
				int packetsRequired = (int) Math.ceil(MC.player.getPos().distanceTo(nextCenterPos) / 10) - 1;

				for (int i = 0; i < packetsRequired; i++) {
					MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, false));
				}

				MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(nextCenterPos.x,
						nextCenterPos.y, nextCenterPos.z, true, false));
				MC.player.setPosition(nextCenterPos);
				break;
			default:
				break;
			}
		} else {
			// Check to see if we actually reached the destination. If not, we want to
			// recalculate the path.
			if (!actualEnd.equals(end)) {
				recalculatePathAsync();
			} else {
				Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
				Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
				clear();
				startButtonText.setText("Calculate");
				startButton.setOnClick(startRunnable);
			}
		}
	}
}