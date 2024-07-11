package net.aoba.gui.tabs;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.event.listeners.TickListener;
import net.aoba.gui.Margin;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.tabs.components.*;
import net.aoba.misc.RenderUtils;
import net.aoba.pathfinding.PathManager;
import net.aoba.pathfinding.PathNode;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

public class GoToTab extends AbstractTab implements TickListener, Render3DListener {
	private ButtonComponent startButton;
	private ButtonComponent setPositionButton;

	private BooleanSetting flyEnabled;
	private StringSetting locationX;
	private StringSetting locationY;
	private StringSetting locationZ;

	private Runnable startRunnable;
	private Runnable clearRunnable;
	private Runnable setPositionRunnable;
	private BlockPos start;
	private BlockPos end;
	private PathManager pathManager;

	private int currentNodeIndex = 0;
	private ArrayList<PathNode> nodes;
	private boolean isStarted = false;

	public GoToTab(String title, int x, int y){
		super(title, x, y, 360, false);


		flyEnabled = new BooleanSetting("goto_fly_enabled", "Fly Enabled", "Fly Enabled", false, var -> {
			pathManager.setFlyAllowed(var);
			if(isStarted)
				recalculatePath();
		});

		locationX = new StringSetting("goto_location_x", "X Coord.", "X Coordinate", "");
		locationY = new StringSetting("goto_location_y", "Y Coord.", "Y Coordinate", "");
		locationZ = new StringSetting("goto_location_z", "Z Coord.", "Z Coordinate", "");

		SettingManager.registerSetting(this.flyEnabled, Aoba.getInstance().settingManager.configContainer);

		SettingManager.registerSetting(this.locationX, Aoba.getInstance().settingManager.configContainer);
		SettingManager.registerSetting(this.locationY, Aoba.getInstance().settingManager.configContainer);
		SettingManager.registerSetting(this.locationZ, Aoba.getInstance().settingManager.configContainer);

		StackPanelComponent stackPanel = new StackPanelComponent(this);
		stackPanel.setMargin(new Margin(null, 30f, null, null));
		
		StringComponent label = new StringComponent("GoTo will automatically walk/fly your player to specific coordinates.", stackPanel);
		stackPanel.addChild(label);

		CheckboxComponent flyEnabledCheckBox = new CheckboxComponent(stackPanel, flyEnabled);
		stackPanel.addChild(flyEnabledCheckBox);

		TextBoxComponent locationXTextBox = new TextBoxComponent(stackPanel, locationX);
		stackPanel.addChild(locationXTextBox);

		TextBoxComponent locationYTextBox = new TextBoxComponent(stackPanel, locationY);
		stackPanel.addChild(locationYTextBox);

		TextBoxComponent locationZTextBox = new TextBoxComponent(stackPanel, locationZ);
		stackPanel.addChild(locationZTextBox);

		this.startRunnable = new Runnable() {
			@Override
		    public void run() {
				startButton.setText("Cancel");
				startButton.setOnClick(clearRunnable);
				isStarted = true;
				recalculatePath();
				registerEvents();
		    }
		};

		this.clearRunnable = new Runnable() {
			@Override
			public void run() {
				unregisterEvents();
				clear();
				startButton.setText("Calculate");
				startButton.setOnClick(startRunnable);
			}
		};

		this.setPositionRunnable = new Runnable() {
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

		this.children.add(stackPanel);

		pathManager = new PathManager();
		pathManager.setFlyAllowed(false);
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
		MinecraftClient MC = MinecraftClient.getInstance();

		if(!NumberUtils.isParsable(locationX.getValue()) || !NumberUtils.isParsable(locationY.getValue()) || !NumberUtils.isParsable(locationZ.getValue()))
			return;

		BlockPos newTarget = new BlockPos(Integer.parseInt(locationX.getValue()), Integer.parseInt(locationY.getValue()), Integer.parseInt(locationZ.getValue()));
		pathManager.setTarget(newTarget);
		nodes = pathManager.recalculatePath(MC.player.getBlockPos());

		start = MC.player.getBlockPos();
		end = newTarget;
		currentNodeIndex = 0;
	}

	private void clear() {
		nodes = null;
		isStarted = false;
	}

	@Override
	public void OnRender(Render3DEvent event) {
		if(this.nodes != null) {
			Box startBox = new Box(start);
			Box endBox = new Box(end);

			RenderUtils.draw3DBox(event.GetMatrix(), startBox, Colors.Red, 1.0f);
			RenderUtils.draw3DBox(event.GetMatrix(), endBox, Colors.Red, 1.0f);

			for(int i = 0; i < nodes.size() - 1; i++) {
				PathNode first = nodes.get(i);
				PathNode second = nodes.get(i + 1);
				RenderUtils.drawLine3D(event.GetMatrix(), first.pos.toCenterPos(), second.pos.toCenterPos(), Colors.Red, 1.0f);
			}
		}
	}

	@Override
	public void OnUpdate(TickEvent event) {
		MinecraftClient MC = MinecraftClient.getInstance();

		if(currentNodeIndex < nodes.size() - 1) {
			// Check next position
			PathNode next = nodes.get(currentNodeIndex + 1);
			BlockPos playerPos = MC.player.getBlockPos();

			if(playerPos.equals(next.pos)) {
				currentNodeIndex++;
				if(currentNodeIndex < nodes.size() - 1)
					next = nodes.get(currentNodeIndex + 1);
				else
					return;
			}

			Vec3d nextCenterPos = next.pos.toCenterPos();

			if(flyEnabled.getValue()) {
				double velocity = Math.min(4f, MC.player.getPos().distanceTo(nextCenterPos));
				Vec3d direction = nextCenterPos.subtract(MC.player.getPos()).normalize().multiply(velocity);
				MC.player.setVelocity(direction);
			}else {
				MC.player.lookAt(EntityAnchor.EYES, new Vec3d(nextCenterPos.x, MC.player.getEyeY(), nextCenterPos.z));
				MC.options.forwardKey.setPressed(true);
				if(next.getWasJump() || MC.player.horizontalCollision)
					MC.options.jumpKey.setPressed(true);
				else
					MC.options.jumpKey.setPressed(false);
			}
		}else {
			Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
			Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
			clear();
			startButton.setText("Calculate");
			startButton.setOnClick(startRunnable);
		}
	}
}