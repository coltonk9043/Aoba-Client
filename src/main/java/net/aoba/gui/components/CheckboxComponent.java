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

import org.joml.Matrix4f;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Size;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class CheckboxComponent extends Component {
	private String text;
	private BooleanSetting checkbox;
	private Runnable onClick;

	public CheckboxComponent(BooleanSetting checkbox) {
		super();
		this.text = checkbox.displayName;
		this.checkbox = checkbox;

		this.setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 30.0f);
	}

	/**
	 * Draws the checkbox to the screen.
	 *
	 * @param drawContext  The current draw context of the game.
	 * @param partialTicks The partial ticks used for interpolation.
	 */
	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();

		// Determine fill color based on checkbox state
		Color fillColor = this.checkbox.getValue() ? new Color(0, 154, 0, 200) : new Color(154, 0, 0, 200);

		Render2D.drawString(drawContext, this.text, actualX, actualY + 8, 0xFFFFFF);
		Render2D.drawOutlinedRoundedBox(matrix4f, actualX + actualWidth - 24, actualY + 5, 20, 20, 3,
				GuiManager.borderColor.getValue(), fillColor);
	}

	/**
	 * Handles updating the Checkbox component.
	 */
	@Override
	public void update() {
		super.update();
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				checkbox.toggle();
				if (onClick != null)
					onClick.run();
				event.cancel();
			}
		}
	}

	public void setChecked(boolean checked) {
		checkbox.setValue(checked);
	}

	public boolean isChecked() {
		return checkbox.getValue();
	}
}
