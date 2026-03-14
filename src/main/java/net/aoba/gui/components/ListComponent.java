/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.List;
import java.util.function.Consumer;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.gui.GridDefinition;
import net.aoba.gui.GridDefinition.RelativeUnit;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.TextAlign;
import net.aoba.gui.colors.Colors;
import net.aoba.settings.types.StringSetting;
import net.aoba.utils.types.MouseButton;

public class ListComponent extends Component implements MouseClickListener {
	private StringSetting listSetting;

	private List<String> itemsSource;
	private int selectedIndex;
	private Consumer<String> onChanged;

	private final StringComponent leftArrow;
	private final StringComponent selectedLabel;
	private final StringComponent rightArrow;

	public ListComponent(List<String> itemsSource) {
		this(itemsSource, (StringSetting) null);
	}

	public ListComponent(List<String> itemsSource, Consumer<String> onChanged) {
		this(itemsSource, (StringSetting) null);
		this.onChanged = onChanged;
	}

	public ListComponent(List<String> itemsSource, StringSetting listSetting) {
		this.itemsSource = itemsSource;
		this.listSetting = listSetting;

		if (listSetting != null) {
			int idx = itemsSource.indexOf(listSetting.getValue());
			if (idx >= 0) this.selectedIndex = idx;
			this.listSetting.addOnUpdate(this::onSettingValueChanged);
		}

		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(30, RelativeUnit.Absolute));
		grid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(30, RelativeUnit.Absolute));

		leftArrow = new StringComponent("<<", GuiManager.foregroundColor.getValue(), false);
		leftArrow.setTextAlign(TextAlign.Center);
		leftArrow.setHeight(30.0f);

		selectedLabel = new StringComponent("", Colors.White, false);
		selectedLabel.setTextAlign(TextAlign.Center);
		selectedLabel.setHeight(30.0f);

		rightArrow = new StringComponent(">>", GuiManager.foregroundColor.getValue(), false);
		rightArrow.setTextAlign(TextAlign.Center);
		rightArrow.setHeight(30.0f);

		grid.addChild(leftArrow);
		grid.addChild(selectedLabel);
		grid.addChild(rightArrow);

		addChild(grid);
		updateSelectedLabel();
	}

	private void onSettingValueChanged(String s) {
		int i = this.itemsSource.indexOf(s);
		if (i >= 0 && i != this.selectedIndex) {
			this.selectedIndex = i;
			updateSelectedLabel();
		}
	}

	private void updateSelectedLabel() {
		if (itemsSource != null && !itemsSource.isEmpty() && selectedIndex < itemsSource.size()) {
			selectedLabel.setText(itemsSource.get(selectedIndex));
		} else {
			selectedLabel.setText("");
		}
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public String getSelectedItem() {
		if (itemsSource.size() > selectedIndex)
			return itemsSource.get(selectedIndex);
		else
			return null;
	}

	public List<String> getItemsSource() {
		return itemsSource;
	}

	public void setItemsSource(List<String> itemsSource) {
		this.itemsSource = itemsSource;
		setSelectedIndex(selectedIndex);
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (isVisible())
			Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
		else
			Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
	}

	public void setSelectedIndex(int index) {
		selectedIndex = index;
		updateSelectedLabel();

		if (listSetting != null)
			listSetting.setValue(itemsSource.get(selectedIndex));
		if (onChanged != null)
			onChanged.accept(itemsSource.get(selectedIndex));
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		Rectangle actualSize = getActualSize();
		if (actualSize != null && actualSize.isDrawable()) {
			if (event.button == MouseButton.LEFT) {
				if (actualSize.getY() < event.mouseY
						&& event.mouseY < actualSize.getY() + actualSize.getHeight()) {

					float mouseX = (float) event.mouseX;
					float actualX = actualSize.getX();
					float actualWidth = actualSize.getWidth();

					if (mouseX > actualX && mouseX < (actualX + 32)) {
						setSelectedIndex(Math.max(selectedIndex - 1, 0));
					} else if (mouseX > (actualX + actualWidth - 32) && mouseX < (actualX + actualWidth))
						setSelectedIndex(Math.min(selectedIndex + 1, itemsSource.size() - 1));

					event.cancel();
				}
			}
		}
	}
}
