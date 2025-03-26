/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.Size;
import net.aoba.settings.types.EnumSetting;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

public class EnumComponent<T extends Enum<T>> extends Component {
	private final EnumSetting<T> enumSetting;

	private boolean hoveringLeftButton;
	private boolean hoveringRightButton;

	public EnumComponent(EnumSetting<T> enumSetting) {
        this.enumSetting = enumSetting;
		header = enumSetting.displayName;
		setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 55.0f);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		float actualX = actualSize.getX();
		float actualY = actualSize.getY();
		float actualWidth = actualSize.getWidth();

		// Draw Header
		if (header != null) {
			Render2D.drawString(drawContext, header, actualX, actualY + 8, 0xFFFFFF);
		}

		// Left Arrow and Right Arrow
		Render2D.drawString(drawContext, "<", actualX, actualY + 34,
				hoveringLeftButton ? GuiManager.foregroundColor.getValue().getColorAsInt() : 0xFFFFFF);
		Render2D.drawString(drawContext, ">", actualX + actualWidth - 8.0f, actualY + 34,
				hoveringRightButton ? GuiManager.foregroundColor.getValue().getColorAsInt() : 0xFFFFFF);

		// Text
		String enumValue = enumSetting.getValue().toString();
		float stringWidth = Render2D.getStringWidth(enumValue);
		Render2D.drawString(drawContext, enumValue, actualX + (actualWidth / 2.0f) - stringWidth, actualY + 34,
				0xFFFFFF);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				T currentValue = enumSetting.getValue();
				T[] enumConstants = currentValue.getDeclaringClass().getEnumConstants();
				int currentIndex = java.util.Arrays.asList(enumConstants).indexOf(currentValue);
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

				enumSetting.setValue(enumConstants[currentIndex]);
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
		Rectangle rightArrowHitbox = new Rectangle(actualX + actualWidth - 12.0f, actualY, 16.0f, actualHeight);

		boolean wasHoveringLeftButton = hoveringLeftButton;
		boolean wasHoveringRightButton = hoveringRightButton;
		hoveringLeftButton = leftArrowHitbox.intersects((float) event.getX(), (float) event.getY());
		hoveringRightButton = rightArrowHitbox.intersects((float) event.getX(), (float) event.getY());

		if (hoveringLeftButton || hoveringRightButton)
			GuiManager.setCursor(CursorStyle.Click);
		else if (wasHoveringLeftButton || wasHoveringRightButton) {
			GuiManager.setCursor(CursorStyle.Default);
		}
	}
}
