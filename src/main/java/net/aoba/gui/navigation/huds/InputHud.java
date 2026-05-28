/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.huds;

import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.components.GridComponent;
import net.aoba.gui.components.RectangleComponent;
import net.aoba.gui.components.StringComponent;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.types.GridDefinition;
import net.aoba.gui.types.GridDefinition.RelativeUnit;
import net.aoba.gui.types.HorizontalAlignment;
import net.aoba.gui.types.ResizeMode;
import net.aoba.gui.types.VerticalAlignment;

public class InputHud extends HudWindow {

	private RectangleComponent rectInputForward;
	private RectangleComponent rectInputLeft;
	private RectangleComponent rectInputBackward;
	private RectangleComponent rectInputRight;
	private RectangleComponent rectInputJump;
	private RectangleComponent rectInputMouseLeft;
	private RectangleComponent rectInputMouseRight;

	public InputHud(int x, int y) {
		super("InputHud", x, y, 50, 24);
		setProperty(UIElement.MinWidthProperty, 200f);
		setProperty(UIElement.MinHeightProperty, 300f);
		resizeMode = ResizeMode.WidthAndHeight;

		GridComponent grid = new GridComponent();

		grid.addRowDefinition(new GridDefinition(2, RelativeUnit.Relative));
		grid.addRowDefinition(new GridDefinition(2, RelativeUnit.Relative));
		grid.addRowDefinition(new GridDefinition(1, RelativeUnit.Relative));
		grid.addRowDefinition(new GridDefinition(1, RelativeUnit.Relative));
		
		grid.setProperty(GridComponent.HorizontalSpacingProperty, 8f);
		grid.setProperty(GridComponent.VerticalSpacingProperty, 8f);
		
		// First Row
		GridComponent firstRow = new GridComponent();

		firstRow.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		firstRow.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		firstRow.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		firstRow.addRowDefinition(new GridDefinition(1, RelativeUnit.Relative));
		firstRow.setProperty(GridComponent.HorizontalSpacingProperty, 8f);

		firstRow.addChild(new StringComponent()); // Empty
		rectInputForward = new RectangleComponent();
		StringComponent rectInputForwardLabel = new StringComponent();
		rectInputForwardLabel.setProperty(StringComponent.TextProperty, "W");
		rectInputForwardLabel.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		rectInputForwardLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		rectInputForward.setContent(rectInputForwardLabel);
		firstRow.addChild(rectInputForward);

		firstRow.addChild(new StringComponent()); // Empty
		grid.addChild(firstRow);
		
		// Second Row
		GridComponent secondRow = new GridComponent();

		secondRow.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		secondRow.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		secondRow.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		secondRow.addRowDefinition(new GridDefinition(1, RelativeUnit.Relative));
		secondRow.setProperty(GridComponent.HorizontalSpacingProperty, 8f);

		rectInputLeft = new RectangleComponent();
		StringComponent rectInputLeftLabel = new StringComponent();
		rectInputLeftLabel.setProperty(StringComponent.TextProperty, "A");
		rectInputLeftLabel.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		rectInputLeftLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		rectInputLeft.setContent(rectInputLeftLabel);
		secondRow.addChild(rectInputLeft);
		
		rectInputBackward = new RectangleComponent();
		StringComponent rectInputBackwardLabel = new StringComponent();
		rectInputBackwardLabel.setProperty(StringComponent.TextProperty, "S");
		rectInputBackwardLabel.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		rectInputBackwardLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		rectInputBackward.setContent(rectInputBackwardLabel);
		secondRow.addChild(rectInputBackward);
		
		rectInputRight = new RectangleComponent();
		StringComponent rectInputRightLabel = new StringComponent();
		rectInputRightLabel.setProperty(StringComponent.TextProperty, "D");
		rectInputRightLabel.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		rectInputRightLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		rectInputRight.setContent(rectInputRightLabel);
		secondRow.addChild(rectInputRight);

		grid.addChild(secondRow);
		
		// Third Row
		rectInputJump = new RectangleComponent();
		StringComponent rectInputJumpLabel = new StringComponent();
		rectInputJumpLabel.setProperty(StringComponent.TextProperty, "Space");
		rectInputJumpLabel.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		rectInputJumpLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		rectInputJump.setContent(rectInputJumpLabel);
		grid.addChild(rectInputJump);
		
		// Fourth Row
		GridComponent fourthRow = new GridComponent();

		fourthRow.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		fourthRow.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		fourthRow.addRowDefinition(new GridDefinition(1, RelativeUnit.Relative));
		fourthRow.setProperty(GridComponent.HorizontalSpacingProperty, 8f);

		rectInputMouseLeft = new RectangleComponent();
		StringComponent rectInputMouseLeftLabel = new StringComponent();
		rectInputMouseLeftLabel.setProperty(StringComponent.TextProperty, "LMB");
		rectInputMouseLeftLabel.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		rectInputMouseLeftLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		rectInputMouseLeft.setContent(rectInputMouseLeftLabel);
		fourthRow.addChild(rectInputMouseLeft);
		
		rectInputMouseRight = new RectangleComponent();
		StringComponent rectInputMouseRightLabel = new StringComponent();
		rectInputMouseRightLabel.setProperty(StringComponent.TextProperty, "RMB");
		rectInputMouseRightLabel.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		rectInputMouseRightLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		rectInputMouseRight.setContent(rectInputMouseRightLabel);
		fourthRow.addChild(rectInputMouseRight);
		
		grid.addChild(fourthRow);
		
		this.setContent(grid);
	}

	@Override
	public void update() {
		super.update();

		if(MC.player.input.keyPresses.forward()){
			rectInputForward.bindProperty(UIElement.BackgroundProperty, GuiManager.buttonHoverBackgroundColor);
		}else {
			rectInputForward.bindProperty(UIElement.BackgroundProperty, GuiManager.windowBackgroundColor);
		}
		
		if(MC.player.input.keyPresses.left()){
			rectInputLeft.bindProperty(UIElement.BackgroundProperty, GuiManager.buttonHoverBackgroundColor);
		}else {
			rectInputLeft.bindProperty(UIElement.BackgroundProperty, GuiManager.windowBackgroundColor);
		}
		
		if(MC.player.input.keyPresses.backward()){
			rectInputBackward.bindProperty(UIElement.BackgroundProperty, GuiManager.buttonHoverBackgroundColor);
		}else {
			rectInputBackward.bindProperty(UIElement.BackgroundProperty, GuiManager.windowBackgroundColor);
		}
		
		if(MC.player.input.keyPresses.right()){
			rectInputRight.bindProperty(UIElement.BackgroundProperty, GuiManager.buttonHoverBackgroundColor);
		}else {
			rectInputRight.bindProperty(UIElement.BackgroundProperty, GuiManager.windowBackgroundColor);
		}
		
		if(MC.player.input.keyPresses.jump()){
			rectInputJump.bindProperty(UIElement.BackgroundProperty, GuiManager.buttonHoverBackgroundColor);
		}else {
			rectInputJump.bindProperty(UIElement.BackgroundProperty, GuiManager.windowBackgroundColor);
		}
		
		if(MC.mouseHandler.isLeftPressed()){
			rectInputMouseLeft.bindProperty(UIElement.BackgroundProperty, GuiManager.buttonHoverBackgroundColor);
		}else {
			rectInputMouseLeft.bindProperty(UIElement.BackgroundProperty, GuiManager.windowBackgroundColor);
		}
		
		if(MC.mouseHandler.isRightPressed()){
			rectInputMouseRight.bindProperty(UIElement.BackgroundProperty, GuiManager.buttonHoverBackgroundColor);
		}else {
			rectInputMouseRight.bindProperty(UIElement.BackgroundProperty, GuiManager.windowBackgroundColor);
		}
	}
}
