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

package net.aoba.gui.tabs.components;

import java.util.List;

import org.joml.Matrix4f;

import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.colors.Color;
import net.aoba.misc.RenderUtils;
import net.aoba.settings.types.StringSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ListComponent extends Component implements LeftMouseDownListener {
	private StringSetting listSetting;

	private List<String> options;
	private int selectedIndex;

	public ListComponent(IGuiElement parent, List<String> options) {
		super(parent);
		this.setLeft(2);
		this.setRight(2);
		this.setHeight(30);
		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);

		this.options = options;
	}

	public ListComponent(IGuiElement parent, List<String> options, StringSetting listSetting) {
		super(parent);
		this.listSetting = listSetting;

		this.setLeft(2);
		this.setRight(2);
		this.setHeight(30);

		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);

		this.options = options;
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		
		float stringWidth = Aoba.getInstance().fontManager.GetRenderer().getWidth(listSetting.getValue());
		RenderUtils.drawString(drawContext, listSetting.getValue(), actualX + (actualWidth / 2.0f) - stringWidth,
				actualY + 8, 0xFFFFFF);
		RenderUtils.drawString(drawContext, "<<", actualX + 8, actualY + 4, 0xFFFFFF);
		RenderUtils.drawString(drawContext, ">>", actualX + 8 + (actualWidth - 34), actualY + 4, 0xFFFFFF);
	}

	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		double mouseX = event.GetMouseX();

		// Mouse is on the left
		if(this.hovered) {			
			if (mouseX > actualX && mouseX < (actualX + 32)) {
				setSelectedIndex(Math.max(--selectedIndex, 0));
			// Mouse is on the right
			} else if (mouseX > (actualX + actualWidth - 32) && mouseX < (actualX + actualWidth)) {
				setSelectedIndex(Math.min(++selectedIndex, options.size() - 1));
			}
		}
	}

	public void setSelectedIndex(int index) {
		selectedIndex = index;
		listSetting.setValue(options.get(selectedIndex));
	}
}
