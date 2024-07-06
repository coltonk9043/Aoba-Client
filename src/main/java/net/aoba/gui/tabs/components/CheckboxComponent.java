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

import org.joml.Matrix4f;
import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.colors.Color;
import net.aoba.misc.RenderUtils;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class CheckboxComponent extends Component implements MouseClickListener {
	private String text;
	private BooleanSetting checkbox;
	private Runnable onClick;
	
	public CheckboxComponent(IGuiElement parent, BooleanSetting checkbox) {
		super(parent);
		this.text = checkbox.displayName;
		this.checkbox = checkbox;

		this.setLeft(2);
		this.setRight(2);
		this.setHeight(30);
	}

	/**
	 * Draws the checkbox to the screen.
	 * @param offset The offset (Y location relative to parent) of the Component.
	 * @param drawContext The current draw context of the game.
	 * @param partialTicks The partial ticks used for interpolation.
	 * @param color The current Color of the UI.
	 */
	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
		
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		
		RenderUtils.drawString(drawContext, this.text, actualX + 6, actualY + 8, 0xFFFFFF);
		if (this.checkbox.getValue()) {
			RenderUtils.drawOutlinedBox(matrix4f, actualX + actualWidth - 24, actualY + 5, 20, 20,
					new Color(0, 154, 0, 200));
		} else {
			RenderUtils.drawOutlinedBox(matrix4f, actualX + actualWidth - 24, actualY + 5, 20, 20,
					new Color(154, 0, 0, 200));
		}
	}

	/**
	 * Handles updating the Checkbox component.
	 * @param offset The offset (Y position relative to parent) of the Checkbox.
	 */
	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void OnVisibilityChanged() {
		if(this.isVisible()) {
			Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
		}else {
			Aoba.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
		}
	}

	@Override
	public void OnMouseClick(MouseClickEvent event) {
		if(event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered && Aoba.getInstance().hudManager.isClickGuiOpen()) {
				checkbox.toggle();
				if(onClick != null) {
					onClick.run();
				}
			}
		}
	}
}
