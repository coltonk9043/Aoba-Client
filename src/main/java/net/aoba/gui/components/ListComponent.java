/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.List;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.Size;
import net.aoba.settings.types.StringSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

public class ListComponent extends Component implements MouseClickListener {
	private StringSetting listSetting;

	private List<String> itemsSource;
	private int selectedIndex;

	public ListComponent(List<String> itemsSource) {
		super();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.itemsSource = itemsSource;
	}

	public ListComponent(List<String> itemsSource, StringSetting listSetting) {
		super();
		this.listSetting = listSetting;
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.itemsSource = itemsSource;
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 30.0f);
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
		setSelectedIndex(this.selectedIndex);
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (this.isVisible())
			Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
		else
			Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();

		if (listSetting != null) {
			float stringWidth = Aoba.getInstance().fontManager.GetRenderer().getWidth(listSetting.getValue());
			Render2D.drawString(drawContext, listSetting.getValue(), actualX + (actualWidth / 2.0f) - stringWidth,
					actualY + 8, 0xFFFFFF);
		} else if (itemsSource.size() > 0) {
			float stringWidth = Aoba.getInstance().fontManager.GetRenderer().getWidth(itemsSource.get(selectedIndex));
			Render2D.drawString(drawContext, itemsSource.get(selectedIndex),
					actualX + (actualWidth / 2.0f) - stringWidth, actualY + 8, 0xFFFFFF);
		}

		Render2D.drawString(drawContext, "<<", actualX + 8, actualY + 4, GuiManager.foregroundColor.getValue());
		Render2D.drawString(drawContext, ">>", actualX + 8 + (actualWidth - 34), actualY + 4,
				GuiManager.foregroundColor.getValue());
	}

	public void setSelectedIndex(int index) {
		selectedIndex = index;

		if (listSetting != null) {
			listSetting.setValue(itemsSource.get(selectedIndex));
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		Rectangle actualSize = this.getActualSize();
		if (actualSize != null && actualSize.isDrawable()) {
			if (event.button == MouseButton.LEFT) {
				if (this.getActualSize().getY() < event.mouseY
						&& event.mouseY < this.getActualSize().getY() + this.getActualSize().getHeight()) {

					float mouseX = (float) event.mouseX;
					float actualX = this.getActualSize().getX();
					float actualWidth = this.getActualSize().getWidth();

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
