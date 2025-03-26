/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.windows;

import java.util.function.Function;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.GridDefinition;
import net.aoba.gui.GridDefinition.RelativeUnit;
import net.aoba.gui.Margin;
import net.aoba.gui.UIElement;
import net.aoba.gui.components.ButtonComponent;
import net.aoba.gui.components.GridComponent;
import net.aoba.gui.components.ItemsComponent;
import net.aoba.gui.components.SeparatorComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.components.TextBoxComponent;
import net.aoba.gui.navigation.Window;
import net.aoba.managers.macros.Macro;

public class MacroWindow extends Window {
	private final ButtonComponent startButton;
	private final StringComponent startButtonText;
	private ButtonComponent replayButton;
	private StringComponent replayButtonText;

	private final TextBoxComponent filenameText;
	private ItemsComponent<Macro> macrosList;
	private final ButtonComponent saveButton;

	private final Runnable startRunnable;
	private final Runnable endRunnable;
	private Runnable replayRunnable;

	public MacroWindow() {
		super("Macro", 895, 150);

		minWidth = 350f;

		StackPanelComponent stackPanel = new StackPanelComponent();

		stackPanel.addChild(new StringComponent("Macros"));
		stackPanel.addChild(new SeparatorComponent());

		StringComponent label = new StringComponent("Records your inputs and plays them back.");
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
		startButton = new ButtonComponent(startRunnable);
		startButtonText = new StringComponent("Record");
		startButton.addChild(startButtonText);
		stackPanel.addChild(startButton);

		stackPanel.addChild(new StringComponent("Filename:"));

		GridComponent fileNameGrid = new GridComponent();
		fileNameGrid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		fileNameGrid.addColumnDefinition(new GridDefinition(75, RelativeUnit.Absolute));

		filenameText = new TextBoxComponent();
		fileNameGrid.addChild(filenameText);

		// Save Button
		saveButton = new ButtonComponent(() ->
        {
            String filename = filenameText.getText();
            if (filename == null || filename.trim().isEmpty()) {
                // Handle the error, e.g., show an error message or set an error state
                filenameText.setErrorState(true);
                return;
            }

            AobaClient aoba = Aoba.getInstance();
            Macro currentMacro = aoba.macroManager.getCurrentlySelected();
            if (currentMacro == null) {
                // Handle the error, e.g., show an error message or set an error state
                filenameText.setErrorState(true);
                return;
            }

            currentMacro.setName(filename);
            aoba.macroManager.addMacro(currentMacro);
			filenameText.setText("");

            // Reload the items control.
            macrosList.setItemsSource(aoba.macroManager.getMacros());
        });

		saveButton.setMargin(new Margin(0f, 2f, 8f, 2f));
		saveButton.addChild(new StringComponent("Save"));
		fileNameGrid.addChild(saveButton);
		stackPanel.addChild(fileNameGrid);

		// Macros List Label
		StringComponent macroListLabel = new StringComponent("Macros");
		stackPanel.addChild(macroListLabel);
		stackPanel.addChild(new SeparatorComponent());

		// Add Macros ItemComponents
		Function<Macro, UIElement> test = (s -> {
			GridComponent macroItemGrid = new GridComponent();
			macroItemGrid.addColumnDefinition(new GridDefinition(1.0f, RelativeUnit.Relative));
			macroItemGrid.addColumnDefinition(new GridDefinition(35f, RelativeUnit.Absolute));
			macroItemGrid.addColumnDefinition(new GridDefinition(35f, RelativeUnit.Absolute));
			macroItemGrid.setMargin(new Margin(4f, 0f, 4f, 0f));

			macroItemGrid.addChild(new StringComponent(s.getName()));

			ButtonComponent playMacroButton = new ButtonComponent(new Runnable() {
				@Override
				public void run() {
					Aoba.getInstance().macroManager.getPlayer().play(s);
				}
			});

			playMacroButton.addChild(new StringComponent("▶"));
			playMacroButton.setMargin(new Margin(2f));
			macroItemGrid.addChild(playMacroButton);

			ButtonComponent deleteMacroButton = new ButtonComponent(new Runnable() {
				@Override
				public void run() {
					Aoba.getInstance().macroManager.removeMacro(s);
					// Reload the items control.
					macrosList.setItemsSource(Aoba.getInstance().macroManager.getMacros());
				}
			});

			deleteMacroButton.addChild(new StringComponent("🗑"));
			deleteMacroButton.setMargin(new Margin(2f));
			macroItemGrid.addChild(deleteMacroButton);

			return macroItemGrid;
		});

		macrosList = new ItemsComponent<Macro>(Aoba.getInstance().macroManager.getMacros(), test);
		stackPanel.addChild(macrosList);

		// Add stackpanel to child.
		addChild(stackPanel);
	}
}