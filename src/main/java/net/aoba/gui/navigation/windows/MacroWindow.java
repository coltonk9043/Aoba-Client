/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.windows;

import java.awt.RenderingHints.Key;
import java.util.function.Function;

import com.mojang.blaze3d.platform.InputConstants;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.GridDefinition;
import net.aoba.gui.GridDefinition.RelativeUnit;
import net.aoba.gui.HorizontalAlignment;
import net.aoba.gui.TextAlign;
import net.aoba.gui.UIElement;
import net.aoba.gui.VerticalAlignment;
import net.aoba.gui.components.ButtonComponent;
import net.aoba.gui.components.CheckboxComponent;
import net.aoba.gui.components.GridComponent;
import net.aoba.gui.components.ItemsComponent;
import net.aoba.gui.components.KeybindComponent;
import net.aoba.gui.components.SeparatorComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.components.TextBoxComponent;
import net.aoba.gui.components.StackPanelComponent.StackType;
import net.aoba.gui.navigation.Window;
import net.aoba.managers.macros.Macro;

public class MacroWindow extends Window {
	private ButtonComponent recordButton;
	private StringComponent recordButtonText;
	private ButtonComponent playPausePlaybackButton;
	private StringComponent playPausePlaybackButtonText;
	private ButtonComponent stopPlaybackButton;
	private StringComponent stopPlaybackButtonText;
	private TextBoxComponent filenameText;
	private ItemsComponent<Macro> macrosList;
	private ButtonComponent saveButton;
	private CheckboxComponent loopCheckbox;
	private KeybindComponent keybindComponent;
	private Macro currentMacro;
	
	private Runnable startRecordingRunnable;
	private Runnable stopRecordingRunnable;
	private Runnable playMacroRunnable;
	private Runnable pauseMacroRunnable;
	private Runnable stopMacroRunnable;
	public MacroWindow() {
		super("Macro", 895, 150);

		minWidth = 350f;

		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(4f);
		stackPanel.addChild(new StringComponent("Macros"));
		stackPanel.addChild(new SeparatorComponent());

		StringComponent label = new StringComponent("Records your inputs and plays them back.");
		stackPanel.addChild(label);

		// Record / Stop Recording Button
		startRecordingRunnable = () -> {
			Aoba.getInstance().guiManager.setClickGuiOpen(false);
			Aoba.getInstance().macroManager.getRecorder().reset();
			Aoba.getInstance().macroManager.getRecorder().startRecording();
			recordButtonText.setText("⏹");
			recordButton.setOnClick(stopRecordingRunnable);
		};

		stopRecordingRunnable = () -> {
			Aoba.getInstance().macroManager.getRecorder().stopRecording();
			Macro macro = AOBA.macroManager.getRecorder().constructMacro();
			this.currentMacro = macro;
			recordButtonText.setText("⏺");
			recordButton.setOnClick(startRecordingRunnable);
		};

		recordButton = new ButtonComponent(startRecordingRunnable);
		recordButton.setWidth(26f);
		recordButton.setHeight(26f);
		recordButtonText = new StringComponent("⏺");
		recordButtonText.setIsHitTestVisible(false);
		recordButtonText.setHorizontalAlignment(HorizontalAlignment.Center);
		recordButtonText.setVerticalAlignment(VerticalAlignment.Center);
		recordButton.addChild(recordButtonText);

		// Play / Pause Button
		playMacroRunnable = () -> {
			playPausePlaybackButtonText.setText("⏸");
			AOBA.macroManager.getPlayer().play(currentMacro, () -> {
				playPausePlaybackButtonText.setText("▶");
				playPausePlaybackButton.setOnClick(playMacroRunnable);
			});
			playPausePlaybackButton.setOnClick(pauseMacroRunnable);
		};
		
		pauseMacroRunnable = () -> {
			playPausePlaybackButtonText.setText("▶");
			AOBA.macroManager.getPlayer().stop();
			playPausePlaybackButton.setOnClick(playMacroRunnable);
		};
		
		playPausePlaybackButton = new ButtonComponent(playMacroRunnable);
		playPausePlaybackButton.setWidth(26f);
		playPausePlaybackButton.setHeight(26f);
		playPausePlaybackButtonText = new StringComponent("▶");
		playPausePlaybackButtonText.setIsHitTestVisible(false);
		playPausePlaybackButtonText.setHorizontalAlignment(HorizontalAlignment.Center);
		playPausePlaybackButtonText.setVerticalAlignment(VerticalAlignment.Center);
		playPausePlaybackButton.addChild(playPausePlaybackButtonText);
		
		// Stop Button
		stopMacroRunnable = () -> {
			AOBA.macroManager.getPlayer().stop();
			playPausePlaybackButtonText.setText("▶");
			playPausePlaybackButton.setOnClick(playMacroRunnable);
		};
		stopPlaybackButton = new ButtonComponent(stopMacroRunnable);
		stopPlaybackButton.setWidth(26f);
		stopPlaybackButton.setHeight(26f);
		stopPlaybackButtonText = new StringComponent("⏹");
		stopPlaybackButtonText.setIsHitTestVisible(false);
		stopPlaybackButtonText.setHorizontalAlignment(HorizontalAlignment.Center);
		stopPlaybackButtonText.setVerticalAlignment(VerticalAlignment.Center);
		stopPlaybackButton.addChild(stopPlaybackButtonText);
		
		StackPanelComponent controlsPanel = new StackPanelComponent();
		controlsPanel.setDirection(StackType.Horizontal);
		controlsPanel.setSpacing(6f);
		controlsPanel.addChild(recordButton);
		controlsPanel.addChild(playPausePlaybackButton);
		controlsPanel.addChild(stopPlaybackButton);

		stackPanel.addChild(controlsPanel);

		// Loop Checkbox
		loopCheckbox = new CheckboxComponent("Loop", false, (looping) -> {
			if (currentMacro != null)
				currentMacro.setLooping(looping);
		});
		stackPanel.addChild(loopCheckbox);

		// Keybind Text
		keybindComponent = new KeybindComponent(InputConstants.UNKNOWN, (key) -> {
			if(currentMacro != null) 
				currentMacro.setKeybind(key);
		});
		stackPanel.addChild(keybindComponent);
		
		// Filename + Save
		stackPanel.addChild(new StringComponent("Filename:"));

		GridComponent fileNameGrid = new GridComponent();
		fileNameGrid.setHorizontalSpacing(4f);
		fileNameGrid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		fileNameGrid.addColumnDefinition(new GridDefinition(75, RelativeUnit.Absolute));
		
		filenameText = new TextBoxComponent();
		fileNameGrid.addChild(filenameText);

		saveButton = new ButtonComponent(() -> {
			String filename = filenameText.getText();
			if (filename == null || filename.trim().isEmpty()) {
				filenameText.setErrorState(true);
				return;
			}

			AobaClient aoba = Aoba.getInstance();
			if (currentMacro == null) {
				filenameText.setErrorState(true);
				return;
			}

			Macro clone = new Macro(currentMacro);
			clone.setName(filename);
			aoba.macroManager.addMacro(clone);
			currentMacro = clone;
			filenameText.setText("");
			loopCheckbox.setChecked(false);
		});

		StringComponent saveText = new StringComponent("Save");
		saveText.setTextAlign(TextAlign.Center);
		saveText.setVerticalAlignment(VerticalAlignment.Center);
		saveButton.addChild(saveText);
		fileNameGrid.addChild(saveButton);
		stackPanel.addChild(fileNameGrid);

		// Macros List
		stackPanel.addChild(new StringComponent("Saved Macros"));
		stackPanel.addChild(new SeparatorComponent());

		Function<Macro, UIElement> macroItemFactory = (macro -> {
			
			GridComponent grid = new GridComponent();
			grid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
			grid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Auto));
			grid.setHorizontalSpacing(4f);
			
			// Macro Name
			String macroNameText = macro.getName();
			if(macro.isLooping())
				macroNameText += " (Loop)";
			
			if(macro.getKeybind() != InputConstants.UNKNOWN)
				macroNameText += " [" + macro.getKeybind().getDisplayName().getString() + "]";
			
			StringComponent text = new StringComponent(macroNameText);
			text.setIsHitTestVisible(true);
			text.setOnClicked((e) -> {
				this.currentMacro = new Macro(macro);
				this.filenameText.setText(macro.getName());
				this.keybindComponent.setKeyBind(macro.getKeybind());
				this.loopCheckbox.setChecked(macro.isLooping());
			});
			grid.addChild(text);
			
			// Delete Button 
			ButtonComponent deleteButton = new ButtonComponent(() -> {
				// TODO: We need to add some kind of confirmation....
				// Let's add a GuiManager POPUP
				AOBA.macroManager.removeMacro(macro);
			});
			
			StringComponent deleteString = new StringComponent("🗑");
			deleteString.setTextAlign(TextAlign.Center);
			deleteString.setVerticalAlignment(VerticalAlignment.Center);
			deleteButton.addChild(deleteString);
			grid.addChild(deleteButton);
			return grid;
		});

		macrosList = new ItemsComponent<Macro>(Aoba.getInstance().macroManager.getMacros(), macroItemFactory);
		stackPanel.addChild(macrosList);

		addChild(stackPanel);
	}
}