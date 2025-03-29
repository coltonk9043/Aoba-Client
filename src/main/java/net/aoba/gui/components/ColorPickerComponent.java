/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.Size;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.ColorSetting.ColorMode;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

public class ColorPickerComponent extends Component {

	private String text;
	private boolean isSliding = false;
	private boolean collapsed = true;
	private ColorSetting color;

	public ColorPickerComponent(String text) {
		this.text = text;

		setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	public ColorPickerComponent(ColorSetting color) {
		text = color.displayName;
		this.color = color;
		setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public void measure(Size availableSize) {
		if (collapsed) {
			preferredSize = new Size(availableSize.getWidth(), 30.0f);
		} else {
			preferredSize = new Size(availableSize.getWidth(), 175.0f);
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT) {
			if (event.action == MouseAction.DOWN) {
				if (hovered) {
					float mouseX = (float) event.mouseX;
					float mouseY = (float) event.mouseY;
					float actualY = actualSize.getY();
					if (mouseY < actualY + 29) {
						collapsed = !collapsed;

						invalidateMeasure();
						/*
						 * if (collapsed) this.setHeight(30f); else this.setHeight(175f);
						 */
						event.cancel();

					} else if (!collapsed) {
						if (mouseY > actualY + 29 && mouseY <= actualY + 59) {
							float actualX = getActualSize().getX();
							float actualWidth = getActualSize().getWidth();

							Rectangle leftButton = new Rectangle(actualX + actualWidth - 128, actualY + 34, 16.0f,
									16.0f);
							Rectangle rightButton = new Rectangle(actualX + actualWidth - 16, actualY + 34, 16.0f,
									16.0f);

							ColorMode[] enumConstants = color.getMode().getDeclaringClass().getEnumConstants();
							int currentIndex = java.util.Arrays.asList(enumConstants).indexOf(color.getMode());
							int enumCount = enumConstants.length;
							if (leftButton.intersects(mouseX, mouseY)) {
								currentIndex = (currentIndex - 1 + enumCount) % enumCount;
							} else if (rightButton.intersects(mouseX, mouseY)) {
								currentIndex = (currentIndex + 1) % enumCount;
							}

							color.setMode(enumConstants[currentIndex]);
						} else if (mouseY > actualY + 59) {
							if (!collapsed)
								isSliding = true;
						}
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

		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();
		float actualHeight = getActualSize().getHeight();

		double mouseX = event.getX();
		double mouseY = event.getY();
		if (Aoba.getInstance().guiManager.isClickGuiOpen() && isSliding) {
			Color colorToModify = color.getValue();
			float vertical = (float) Math
					.min(Math.max(1.0f - (((mouseY - (actualY + 59)) - 1) / (actualHeight - 63)), 0.0f), 1.0f);

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
		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();
		float actualHeight = getActualSize().getHeight();

		Render2D.drawString(drawContext, text, actualX, actualY + 8, 0xFFFFFF);
		Render2D.drawString(drawContext, collapsed ? ">>" : "<<", (actualX + actualWidth - 24), actualY + 8,
				GuiManager.foregroundColor.getValue().getColorAsInt());

		if (!collapsed) {
			// Mode
			Render2D.drawString(drawContext, "Mode", actualX, actualY + 34, 0xFFFFFF);
			Render2D.drawString(drawContext, "<", actualX + actualWidth - 128, actualY + 34, 0xFFFFFF);
			Render2D.drawString(drawContext, ">", actualX + actualWidth - 16, actualY + 34, 0xFFFFFF);

			String enumText = color.getMode().name();
			float stringLength = Render2D.getStringWidth(enumText);
			Render2D.drawString(drawContext, enumText, actualX + actualWidth - 70 - stringLength, actualY + 34,
					0xFFFFFF);

			// Gradients
			Color newColor = new Color(255, 0, 0);
			Color colorSetting = color.getValue();
			newColor.setHSV(colorSetting.getHue(), 1.0f, 1.0f);
			Render2D.drawHorizontalGradient(drawContext, actualX, actualY + 59, actualWidth - 76, actualHeight - 63,
					new Color(255, 255, 255), newColor);
			Render2D.drawVerticalGradient(drawContext, actualX, actualY + 59, actualWidth - 76, actualHeight - 63,
					new Color(0, 0, 0, 0), new Color(0, 0, 0));

			// Draw Hue Rectangle
			float increment = ((actualHeight - 63) / 6.0f);
			Render2D.drawVerticalGradient(drawContext, actualX + actualWidth - 68, actualY + 59, 30, increment,
					new Color(255, 0, 0), new Color(255, 255, 0));
			Render2D.drawVerticalGradient(drawContext, actualX + actualWidth - 68, actualY + 59 + increment, 30,
					increment, new Color(255, 255, 0), new Color(0, 255, 0));
			Render2D.drawVerticalGradient(drawContext, actualX + actualWidth - 68, actualY + 59 + (2 * increment), 30,
					increment, new Color(0, 255, 0), new Color(0, 255, 255));
			Render2D.drawVerticalGradient(drawContext, actualX + actualWidth - 68, actualY + 59 + (3 * increment), 30,
					increment, new Color(0, 255, 255), new Color(0, 0, 255));
			Render2D.drawVerticalGradient(drawContext, actualX + actualWidth - 68, actualY + 59 + (4 * increment), 30,
					increment, new Color(0, 0, 255), new Color(255, 0, 255));
			Render2D.drawVerticalGradient(drawContext, actualX + actualWidth - 68, actualY + 59 + (5 * increment), 30,
					increment, new Color(255, 0, 255), new Color(255, 0, 0));

			// Draw Alpha Rectangle
			Render2D.drawVerticalGradient(drawContext, actualX + actualWidth - 30, actualY + 59, 30, actualHeight - 63,
					new Color(255, 255, 255), new Color(0, 0, 0));

			// Draw Outlines
			Render2D.drawBoxOutline(drawContext, actualX, actualY + 59, actualWidth - 76, actualHeight - 63,
					Colors.Black);
			Render2D.drawBoxOutline(drawContext, actualX + actualWidth - 68, actualY + 59, 30, actualHeight - 63,
					Colors.Black);
			Render2D.drawBoxOutline(drawContext, actualX + actualWidth - 30, actualY + 59, 30, actualHeight - 63,
					Colors.Black);

			// Draw Indicators
			Render2D.drawCircle(drawContext, actualX + (colorSetting.getSaturation() * (actualWidth - 76)),
					actualY + 59 + ((1.0f - colorSetting.getLuminance()) * (actualHeight - 63)), 3,
					new Color(255, 255, 255, 255));
			Render2D.drawOutlinedBox(drawContext, actualX + actualWidth - 68,
					actualY + 59 + ((colorSetting.getHue() / 360.0f) * (actualHeight - 63)), 30, 3, Colors.Black,
					new Color(255, 255, 255, 255));
			Render2D.drawOutlinedBox(drawContext, actualX + actualWidth - 30,
					actualY + 59 + (((255.0f - (colorSetting.getAlpha() * 255)) / 255.0f) * (actualHeight - 63)), 30, 3,
					Colors.Black, new Color(255, 255, 255, 255));
		}
	}

}