package net.aoba.gui.navigation.windows;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.Margin;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.components.*;
import net.aoba.gui.navigation.Window;
import net.aoba.utils.render.Render3D;
import net.aoba.pathfinding.AbstractPathManager;
import net.aoba.pathfinding.FlyPathManager;
import net.aoba.pathfinding.PathNode;
import net.aoba.pathfinding.TeleportPathManager;
import net.aoba.pathfinding.WalkingPathManager;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.StringSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import org.apache.commons.lang3.math.NumberUtils;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoToWindow extends Window implements TickListener, Render3DListener {
	
	public enum Pathfinder {
		Walk,
		Fly,
		Teleport,
	}
	
	private static MinecraftClient MC = MinecraftClient.getInstance();
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();

	private ButtonComponent startButton;
	private ButtonComponent setPositionButton;
	private SliderComponent radiusSlider;
	private SliderComponent flyMaxSpeedSlider;
	
	private EnumSetting<Pathfinder> pathfinderMode;
	private BooleanSetting avoidWater;
	private BooleanSetting avoidLava;
	private FloatSetting radius;
	private StringSetting locationX;
	private StringSetting locationY;
	private StringSetting locationZ;
	private FloatSetting maxSpeed;

	private Runnable startRunnable;
	private Runnable clearRunnable;
	private Runnable setPositionRunnable;
	private BlockPos start;
	private BlockPos end;
	private BlockPos actualEnd;
	private AbstractPathManager pathManager;

	private int currentNodeIndex = 0;
	private ArrayList<PathNode> nodes;
	private boolean isStarted = false;

	public GoToWindow(String title, int x, int y) {
		super(title, x, y, 360, 0);

		this.inheritHeightFromChildren = true;
		
		pathfinderMode = new EnumSetting<Pathfinder>("goto_pathfinder_mode", "Mode", Pathfinder.Walk, var -> {
			switch(var) {
			case Pathfinder.Fly:
				pathManager = new FlyPathManager();
				break;
			case Pathfinder.Walk:
				pathManager = new WalkingPathManager();
				break;
			case Pathfinder.Teleport:
				pathManager = new TeleportPathManager();
				break;
			}

			// Disable Radius setting if not teleport
			radiusSlider.setVisible(var == Pathfinder.Teleport);
			flyMaxSpeedSlider.setVisible(var == Pathfinder.Fly);
			
			if (isStarted)
				recalculatePathAsync();
		});

		avoidWater = new BooleanSetting("goto_avoid_water", "Avoid Water", "Avoid Water", false, var -> {
			pathManager.setAvoidWater(var);
			if (isStarted)
				recalculatePathAsync();
		});

		avoidLava = new BooleanSetting("goto_avoid_lava", "Avoid Lava", "Avoid Lava", true, var -> {
			pathManager.setAvoidLava(var);
			if (isStarted)
				recalculatePathAsync();
		});

		radius = new FloatSetting("goto_radius", "Teleport Radius", "The radius that the teleport pathfinder will attempt to find a block within.", 5.0f, 1.0f, 100.0f, 1.0f, var -> {
			if(pathManager instanceof TeleportPathManager) {
				TeleportPathManager tpManager = (TeleportPathManager)pathManager;
				tpManager.setRadius(var);
				if (isStarted)
					recalculatePathAsync();
			}
		});
	
		locationX = new StringSetting("goto_location_x", "X Coord.", "X Coordinate", "");
		locationY = new StringSetting("goto_location_y", "Y Coord.", "Y Coordinate", "");
		locationZ = new StringSetting("goto_location_z", "Z Coord.", "Z Coordinate", "");
		maxSpeed = new FloatSetting("goto_max_speed", "Max Speed", "Max Speed", 4.0f, 0.5f, 15.0f, 0.5f);

		// Register Settings
		SettingManager.registerSetting(this.pathfinderMode, Aoba.getInstance().settingManager.configContainer);
		SettingManager.registerSetting(this.avoidWater, Aoba.getInstance().settingManager.configContainer);
		SettingManager.registerSetting(this.avoidLava, Aoba.getInstance().settingManager.configContainer);
		SettingManager.registerSetting(this.radius, Aoba.getInstance().settingManager.configContainer);
		SettingManager.registerSetting(this.locationX, Aoba.getInstance().settingManager.configContainer);
		SettingManager.registerSetting(this.locationY, Aoba.getInstance().settingManager.configContainer);
		SettingManager.registerSetting(this.locationZ, Aoba.getInstance().settingManager.configContainer);
		SettingManager.registerSetting(this.maxSpeed, Aoba.getInstance().settingManager.configContainer);

		StackPanelComponent stackPanel = new StackPanelComponent(this);
		stackPanel.setMargin(new Margin(null, 30f, null, null));

		StringComponent label = new StringComponent(
				"GoTo will automatically walk/fly your player to specific coordinates.", stackPanel);
		stackPanel.addChild(label);

		EnumComponent<Pathfinder> pathfinderModeComponent = new EnumComponent<Pathfinder>(stackPanel, pathfinderMode);
		stackPanel.addChild(pathfinderModeComponent);

		CheckboxComponent avoidWaterCheckbox = new CheckboxComponent(stackPanel, avoidWater);
		stackPanel.addChild(avoidWaterCheckbox);

		CheckboxComponent avoidLavaCheckbox = new CheckboxComponent(stackPanel, avoidLava);
		stackPanel.addChild(avoidLavaCheckbox);

		radiusSlider = new SliderComponent(stackPanel, radius);
		stackPanel.addChild(radiusSlider);
		
		flyMaxSpeedSlider = new SliderComponent(stackPanel, maxSpeed);
		stackPanel.addChild(flyMaxSpeedSlider);

		TextBoxComponent locationXTextBox = new TextBoxComponent(stackPanel, locationX);
		stackPanel.addChild(locationXTextBox);

		TextBoxComponent locationYTextBox = new TextBoxComponent(stackPanel, locationY);
		stackPanel.addChild(locationYTextBox);

		TextBoxComponent locationZTextBox = new TextBoxComponent(stackPanel, locationZ);
		stackPanel.addChild(locationZTextBox);

		startRunnable = new Runnable() {
			@Override
			public void run() {
				startButton.setText("Cancel");
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
				startButton.setText("Calculate");
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

		startButton = new ButtonComponent(stackPanel, "Calculate", startRunnable);
		stackPanel.addChild(startButton);

		setPositionButton = new ButtonComponent(stackPanel, "Set Position", setPositionRunnable);
		stackPanel.addChild(setPositionButton);

		children.add(stackPanel);

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
			
			if ((isTeleportMode && !state.isAir() && !state.getFluidState().isIn(FluidTags.WATER) && !state.getFluidState().isIn(FluidTags.LAVA) && !(state.getBlock() instanceof PlantBlock)) 
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
	public void OnRender(Render3DEvent event) {
		if (this.nodes != null) {
			Box startBox = new Box(start);
			Box endBox = new Box(end);

			Render3D.draw3DBox(event.GetMatrix(), startBox, Colors.Red, 1.0f);
			Render3D.draw3DBox(event.GetMatrix(), endBox, Colors.Red, 1.0f);

			for (int i = 0; i < nodes.size() - 1; i++) {
				PathNode first = nodes.get(i);
				PathNode second = nodes.get(i + 1);

				Vec3d pos1 = first.pos.toCenterPos().add(-0.15f, -0.15f, -0.15f);
				Vec3d pos2 = first.pos.toCenterPos().add(0.15f, 0.15f, 0.15f);
				Box box = new Box(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);

				Render3D.draw3DBox(event.GetMatrix(), box, Colors.Red, 1);
				Render3D.drawLine3D(event.GetMatrix(), first.pos.toCenterPos(), second.pos.toCenterPos(), Colors.Red,
						1.0f);
			}
		}
	}

	@Override
	public void OnUpdate(TickEvent event) {
		MinecraftClient MC = MinecraftClient.getInstance();
		if (nodes == null)
			return;

		if (currentNodeIndex < nodes.size() - 1) {
			// Check next position
			PathNode next = nodes.get(currentNodeIndex + 1);
			BlockPos playerPos;
			
			if(MC.player.isRiding()) {
				Entity riding = MC.player.getRootVehicle();
				playerPos = riding.getBlockPos();
			}
			else
				playerPos= MC.player.getBlockPos();

			if (playerPos.equals(next.pos)) {
				currentNodeIndex++;
				if (currentNodeIndex < nodes.size() - 1)
					next = nodes.get(currentNodeIndex + 1);
				else
					return;
			}

			Vec3d nextCenterPos = next.pos.toBottomCenterPos();

			switch(pathfinderMode.getValue()) {
				case Pathfinder.Fly:
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
				case Pathfinder.Walk:
					MC.player.getAbilities().flying = false;
					MC.player.lookAt(EntityAnchor.EYES, new Vec3d(nextCenterPos.x, MC.player.getEyeY(), nextCenterPos.z));
					MC.options.forwardKey.setPressed(true);
					if (next.getIsInWater() || next.getIsInLava() || next.getWasJump() || MC.player.horizontalCollision)
						MC.options.jumpKey.setPressed(true);
					else
						MC.options.jumpKey.setPressed(false);
					break;
				case Pathfinder.Teleport:
	                int packetsRequired = (int) Math.ceil(MC.player.getPos().distanceTo(nextCenterPos) / 10) - 1;

	                for (int i = 0; i < packetsRequired; i++) {
	                    MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
	                }

	                MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(nextCenterPos.x, nextCenterPos.y, nextCenterPos.z, true));
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
				startButton.setText("Calculate");
				startButton.setOnClick(startRunnable);
			}
		}
	}
}