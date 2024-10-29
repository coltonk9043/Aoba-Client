package net.aoba.gui.navigation.windows;

import net.aoba.Aoba;
import net.aoba.AobaClient;
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

public class MacroWindow extends Window {
	private static MinecraftClient MC = MinecraftClient.getInstance();

	private ButtonComponent startButton;
	private ButtonComponent replayButton;
	
	private Runnable startRunnable;
	private Runnable endRunnable;
	private Runnable replayRunnable;

	public MacroWindow(int x, int y) {
		super("Macro", x, y, 360, 0);

		this.inheritHeightFromChildren = true;


		StackPanelComponent stackPanel = new StackPanelComponent(this);
		stackPanel.setMargin(new Margin(null, 30f, null, null));

		StringComponent label = new StringComponent(
				"Records your inputs and plays them back.", stackPanel);
		stackPanel.addChild(label);

		startRunnable = new Runnable() {
			@Override
			public void run() {
				Aoba.getInstance().guiManager.setClickGuiOpen(false);
				Aoba.getInstance().macroManager.getRecorder().startRecording();
				startButton.setText("Stop Recording");
				startButton.setOnClick(endRunnable);
			}
		};

		endRunnable = new Runnable() {
			@Override
			public void run() {
				Aoba.getInstance().macroManager.getRecorder().stopRecording();
				startButton.setText("Record");
				startButton.setOnClick(startRunnable);
			}
		};
		startButton = new ButtonComponent(stackPanel, "Record", startRunnable);
		stackPanel.addChild(startButton);

		
		replayRunnable = new Runnable() {
			@Override
			public void run() {
				AobaClient aoba = Aoba.getInstance();
				aoba.macroManager.getPlayer().play(aoba.macroManager.getCurrentlySelected());
			}
		};
		replayButton = new ButtonComponent(stackPanel, "Replay", replayRunnable);
		stackPanel.addChild(replayButton);
		
		children.add(stackPanel);
	}
}