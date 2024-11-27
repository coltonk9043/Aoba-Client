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
import net.aoba.gui.UIElement;
import net.aoba.gui.colors.Color;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ButtonComponent extends Component {

	private Runnable onClick;

	/**
	 * Constructor for button component.
	 *
	 * @param parent  Parent Tab that this Component resides in.
	 * @param text    Text contained in this button element.
	 * @param onClick OnClick delegate that will run when the button is pressed.
	 */
	public ButtonComponent(UIElement parent, Runnable onClick) {
		super(parent);

		this.setMargin(new Margin(8f, 4f, 8f, 4f));

		this.onClick = onClick;
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 40.0f);
	}

	/**
	 * Sets the OnClick delegate of the button.
	 *
	 * @param onClick Delegate to set.
	 */
	public void setOnClick(Runnable onClick) {
		this.onClick = onClick;
	}

	/**
	 * Draws the button to the screen.
	 *
	 * @param drawContext  The current draw context of the game.
	 * @param partialTicks The partial ticks used for interpolation.
	 */
	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();
		float actualHeight = this.getActualSize().getHeight();

		Color color = GuiManager.foregroundColor.getValue();
		if (hovered) {
			color = color.add(45, 45, 45);
		}

		Render2D.drawOutlinedRoundedBox(matrix4f, actualX, actualY, actualWidth, actualHeight, 3.0f,
				GuiManager.borderColor.getValue(), color);

		super.draw(drawContext, partialTicks);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (this.hovered) {
				if (onClick != null)
					onClick.run();
				event.cancel();
			}
		}
	}
}
