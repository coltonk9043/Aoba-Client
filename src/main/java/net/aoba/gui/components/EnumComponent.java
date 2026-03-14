/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.function.Consumer;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.GridDefinition;
import net.aoba.gui.GridDefinition.RelativeUnit;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.TextAlign;
import net.aoba.gui.colors.Colors;
import net.aoba.settings.types.EnumSetting;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class EnumComponent<T extends Enum<T>> extends Component {
	private T value;
	private final T[] enumConstants;
	private EnumSetting<T> setting;
	private Consumer<T> onChanged;

	private boolean hoveringLeftButton;
	private boolean hoveringRightButton;

	private StringComponent headerLabel;
	private StringComponent leftArrow;
	private StringComponent selectedLabel;
	private StringComponent rightArrow;

	public EnumComponent(T value, Consumer<T> onChanged) {
		this.value = value;
		this.enumConstants = value.getDeclaringClass().getEnumConstants();
		this.onChanged = onChanged;

		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(16, RelativeUnit.Absolute));
		grid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(16, RelativeUnit.Absolute));

		leftArrow = new StringComponent("<", Colors.White, false);
		leftArrow.setTextAlign(TextAlign.Center);
		leftArrow.setHeight(25.0f);

		selectedLabel = new StringComponent(value.toString(), Colors.White, false);
		selectedLabel.setTextAlign(TextAlign.Center);
		selectedLabel.setHeight(25.0f);

		rightArrow = new StringComponent(">", Colors.White, false);
		rightArrow.setTextAlign(TextAlign.Center);
		rightArrow.setHeight(25.0f);

		grid.addChild(leftArrow);
		grid.addChild(selectedLabel);
		grid.addChild(rightArrow);

		addChild(grid);
	}

	public EnumComponent(EnumSetting<T> setting) {
		this.value = setting.getValue();
		this.enumConstants = value.getDeclaringClass().getEnumConstants();
		this.setting = setting;
		this.setting.addOnUpdate(this::onSettingValueChanged);
		header = setting.displayName;

		StackPanelComponent stack = new StackPanelComponent();
		stack.setSpacing(4f);
		headerLabel = new StringComponent(header, Colors.White, false);
		stack.addChild(headerLabel);

		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(16, RelativeUnit.Absolute));
		grid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(16, RelativeUnit.Absolute));

		leftArrow = new StringComponent("<", Colors.White, false);
		leftArrow.setTextAlign(TextAlign.Center);
		leftArrow.setHeight(25.0f);

		selectedLabel = new StringComponent(value.toString(), Colors.White, false);
		selectedLabel.setTextAlign(TextAlign.Center);
		selectedLabel.setHeight(25.0f);

		rightArrow = new StringComponent(">", Colors.White, false);
		rightArrow.setTextAlign(TextAlign.Center);
		rightArrow.setHeight(25.0f);

		grid.addChild(leftArrow);
		grid.addChild(selectedLabel);
		grid.addChild(rightArrow);
		stack.addChild(grid);

		addChild(stack);
	}

	private void onSettingValueChanged(T v) {
		if (v != this.value) {
			this.value = v;
			updateSelectedLabel();
		}
	}

	private void updateSelectedLabel() {
		selectedLabel.setText(value.toString());
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
		updateSelectedLabel();
		if (setting != null)
			setting.setValue(value);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				int currentIndex = java.util.Arrays.asList(enumConstants).indexOf(value);
				int enumCount = enumConstants.length;

				float actualX = actualSize.getX();
				float actualY = actualSize.getY();
				float actualWidth = actualSize.getWidth();
				float actualHeight = actualSize.getHeight();

				Rectangle leftArrowHitbox = new Rectangle(actualX, actualY, 16.0f, actualHeight);
				Rectangle rightArrowHitbox = new Rectangle(actualX + actualWidth - 16.0f, actualY, 16.0f, actualHeight);
				if (leftArrowHitbox.intersects((float) event.mouseX, (float) event.mouseY))
					currentIndex = (currentIndex - 1 + enumCount) % enumCount;
				else if (rightArrowHitbox.intersects((float) event.mouseX, (float) event.mouseY))
					currentIndex = (currentIndex + 1) % enumCount;

				value = enumConstants[currentIndex];
				updateSelectedLabel();
				if (setting != null)
					setting.setValue(value);
				if (onChanged != null)
					onChanged.accept(value);
				event.cancel();
			}
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		super.onMouseMove(event);

		float actualX = actualSize.getX();
		float actualY = actualSize.getY();
		float actualWidth = actualSize.getWidth();
		float actualHeight = actualSize.getHeight();

		Rectangle leftArrowHitbox = new Rectangle(actualX, actualY, 16.0f, actualHeight);
		Rectangle rightArrowHitbox = new Rectangle(actualX + actualWidth - 16.0f, actualY, 16.0f, actualHeight);

		boolean wasHoveringLeftButton = hoveringLeftButton;
		boolean wasHoveringRightButton = hoveringRightButton;
		hoveringLeftButton = leftArrowHitbox.intersects((float) event.getX(), (float) event.getY());
		hoveringRightButton = rightArrowHitbox.intersects((float) event.getX(), (float) event.getY());

		leftArrow.setColor(hoveringLeftButton ? GuiManager.foregroundColor.getValue() : Colors.White);
		rightArrow.setColor(hoveringRightButton ? GuiManager.foregroundColor.getValue() : Colors.White);

		if (hoveringLeftButton || hoveringRightButton)
			GuiManager.setCursor(CursorStyle.Click);
		else if (wasHoveringLeftButton || wasHoveringRightButton) {
			GuiManager.setCursor(CursorStyle.Default);
		}
	}
}
