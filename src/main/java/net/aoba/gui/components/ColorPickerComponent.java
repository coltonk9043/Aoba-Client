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

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.aoba.settings.types.ColorSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class ColorPickerComponent extends Component {

	private String text;
	private boolean isSliding = false;
	private boolean collapsed = true;
	private ColorSetting color;

	public ColorPickerComponent(String text, IGuiElement parent) {
		super(parent, new Rectangle(null, null, null, 30f));
		this.text = text;

		this.setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	public ColorPickerComponent(IGuiElement parent, ColorSetting color) {
		super(parent, new Rectangle(null, null, null, 30f));
		this.text = color.displayName;
		this.color = color;
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
	}


	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT) {
			if (event.action == MouseAction.DOWN) {
				if (hovered) {
					double mouseY = event.mouseY;
					float actualY = actualSize.getY();
					if (mouseY < actualY + 29) {
						collapsed = !collapsed;
						if (collapsed)
							this.setHeight(30f);
						else
							this.setHeight(145f);
						event.cancel();
					} else {
						if (!collapsed)
							isSliding = true;
					}
					event.cancel();
				}
			} else if (event.action == MouseAction.UP) {
				isSliding = false;
			}
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		super.onMouseMove(event);

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();
		float actualHeight = this.getActualSize().getHeight();

		double mouseX = event.getX();
		double mouseY = event.getY();
		if (Aoba.getInstance().hudManager.isClickGuiOpen() && this.isSliding) {
			Color colorToModify = color.getValue();
			float vertical = (float) Math
					.min(Math.max(1.0f - (((mouseY - (actualY + 29)) - 1) / (actualHeight - 33)), 0.0f), 1.0f);

			// If inside of saturation/lightness box.
			if (mouseX >= actualX && mouseX <= actualX + actualWidth - 76) {
				float horizontal = (float) Math.min(Math.max(((mouseX - (actualX)) - 1) / (actualWidth - 76), 0.0f),
						1.0f);

				colorToModify.setLuminance(vertical);
				colorToModify.setSaturation(horizontal);
			} else if (mouseX >= actualX + actualWidth - 68 && mouseX <= actualX + actualWidth - 34) {
				colorToModify.setHue((1.0f - vertical) * 360.0f);
			} else if (mouseX >= actualX + actualWidth - 30 && mouseX <= actualX + actualWidth) {
				colorToModify.setAlpha(Math.round(vertical * 255.0f));
			}
		}
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();
		float actualHeight = this.getActualSize().getHeight();

		Render2D.drawString(drawContext, this.text, actualX, actualY + 8, 0xFFFFFF);
		Render2D.drawString(drawContext, collapsed ? ">>" : "<<", (actualX + actualWidth - 24), actualY + 8,
				GuiManager.foregroundColor.getValue().getColorAsInt());

		if (!collapsed) {
			Color newColor = new Color(255, 0, 0);
			Color colorSetting = this.color.getValue();
			newColor.setHSV(colorSetting.getHue(), 1.0f, 1.0f);
			Render2D.drawHorizontalGradient(matrix4f, actualX, actualY + 29, actualWidth - 76, actualHeight - 33,
					new Color(255, 255, 255), newColor);
			Render2D.drawVerticalGradient(matrix4f, actualX, actualY + 29, actualWidth - 76, actualHeight - 33,
					new Color(0, 0, 0, 0), new Color(0, 0, 0));

			// Draw Hue Rectangle
			float increment = ((actualHeight - 33) / 6.0f);
			Render2D.drawVerticalGradient(matrix4f, actualX + actualWidth - 68, actualY + 29, 30, increment,
					new Color(255, 0, 0), new Color(255, 255, 0));
			Render2D.drawVerticalGradient(matrix4f, actualX + actualWidth - 68, actualY + 29 + increment, 30,
					increment, new Color(255, 255, 0), new Color(0, 255, 0));
			Render2D.drawVerticalGradient(matrix4f, actualX + actualWidth - 68, actualY + 29 + (2 * increment), 30,
					increment, new Color(0, 255, 0), new Color(0, 255, 255));
			Render2D.drawVerticalGradient(matrix4f, actualX + actualWidth - 68, actualY + 29 + (3 * increment), 30,
					increment, new Color(0, 255, 255), new Color(0, 0, 255));
			Render2D.drawVerticalGradient(matrix4f, actualX + actualWidth - 68, actualY + 29 + (4 * increment), 30,
					increment, new Color(0, 0, 255), new Color(255, 0, 255));
			Render2D.drawVerticalGradient(matrix4f, actualX + actualWidth - 68, actualY + 29 + (5 * increment), 30,
					increment, new Color(255, 0, 255), new Color(255, 0, 0));

			// Draw Alpha Rectangle
			Render2D.drawVerticalGradient(matrix4f, actualX + actualWidth - 30, actualY + 29, 30, actualHeight - 33,
					new Color(255, 255, 255), new Color(0, 0, 0));

			// Draw Outlines
			Render2D.drawBoxOutline(matrix4f, actualX, actualY + 29, actualWidth - 76, actualHeight - 33, Colors.Black);
			Render2D.drawBoxOutline(matrix4f, actualX + actualWidth - 68, actualY + 29, 30, actualHeight - 33, Colors.Black);
			Render2D.drawBoxOutline(matrix4f, actualX + actualWidth - 30, actualY + 29, 30, actualHeight - 33, Colors.Black);

			// Draw Indicators
			Render2D.drawCircle(matrix4f, actualX + (colorSetting.getSaturation() * (actualWidth - 76)),
					actualY + 29 + ((1.0f - colorSetting.getLuminance()) * (actualHeight - 33)), 3, new Color(255, 255, 255, 255));
			Render2D.drawOutlinedBox(matrix4f, actualX + actualWidth - 68,
					actualY + 29 + ((colorSetting.getHue() / 360.0f) * (actualHeight - 33)), 30, 3, Colors.Black,new Color(255, 255, 255, 255));
			Render2D.drawOutlinedBox(matrix4f, actualX + actualWidth - 30,
					actualY + 29 + (((255.0f - (colorSetting.getAlpha() * 255)) / 255.0f) * (actualHeight - 33)), 30, 3, Colors.Black,
					new Color(255, 255, 255, 255));
		}
	}


}