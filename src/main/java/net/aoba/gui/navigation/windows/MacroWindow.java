package net.aoba.gui.navigation.windows;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.components.ButtonComponent;
import net.aoba.gui.components.SeparatorComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.navigation.Window;

public class MacroWindow extends Window {
	private ButtonComponent startButton;
	private StringComponent startButtonText;
	private ButtonComponent replayButton;
	private StringComponent replayButtonText;

	private Runnable startRunnable;
	private Runnable endRunnable;
	private Runnable replayRunnable;

	public MacroWindow() {
		super("Macro", 895, 150);

		this.minWidth = 350f;

		StackPanelComponent stackPanel = new StackPanelComponent(this);

		stackPanel.addChild(new StringComponent(stackPanel, "Macros"));
		stackPanel.addChild(new SeparatorComponent(stackPanel));

		StringComponent label = new StringComponent(stackPanel, "Records your inputs and plays them back.");
		stackPanel.addChild(label);

		startRunnable = new Runnable() {
			@Override
			public void run() {
				Aoba.getInstance().guiManager.setClickGuiOpen(false);
				Aoba.getInstance().macroManager.getRecorder().startRecording();
				startButtonText.setText("Stop Recording");
				startButton.setOnClick(endRunnable);
			}
		};

		endRunnable = new Runnable() {
			@Override
			public void run() {
				Aoba.getInstance().macroManager.getRecorder().stopRecording();
				startButtonText.setText("Record");
				startButton.setOnClick(startRunnable);
			}
		};
		startButton = new ButtonComponent(stackPanel, startRunnable);
		startButtonText = new StringComponent(startButton, "Record");
		startButton.addChild(startButtonText);
		stackPanel.addChild(startButton);

		replayRunnable = new Runnable() {
			@Override
			public void run() {
				AobaClient aoba = Aoba.getInstance();
				aoba.macroManager.getPlayer().play(aoba.macroManager.getCurrentlySelected());
			}
		};
		replayButton = new ButtonComponent(stackPanel, replayRunnable);
		replayButtonText = new StringComponent(startButton, "Replay");
		replayButton.addChild(replayButtonText);
		stackPanel.addChild(replayButton);

		// Add stackpanel to child.
		addChild(stackPanel);
	}
}