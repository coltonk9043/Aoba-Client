/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.managers.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

public class PinnableWindow extends Window {
	protected BooleanSetting isPinned;

	public PinnableWindow(String ID, float x, float y) {
		super(ID, x, y);

		isPinned = BooleanSetting.builder().id(ID + "_pinned").defaultValue(false).build();

		SettingManager.registerSetting(isPinned);
	}

	public final boolean isPinned() {
		return isPinned.getValue();
	}

	public final void setPinned(boolean pin) {
		isPinned.setValue(pin);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		// Check to see if the event is cancelled. If not, execute branch.
		if (!event.isCancelled()) {
			if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
				float mouseX = (float) event.mouseX;
				float mouseY = (float) event.mouseY;

				Rectangle pos = position.getValue();

				Rectangle pinHitbox = new Rectangle(pos.getX() + pos.getWidth() - 24, pos.getY() + 4, 16.0f, 16.0f);
				if (pinHitbox.intersects(mouseX, mouseY)) {
					isPinned.setValue(!isPinned.getValue());
					event.cancel();
					return;
				}
			}
		}

		// Cancel any other movements if it is pinned.
		if (isPinned())
			event.cancel();

		super.onMouseClick(event);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		Rectangle pos = position.getValue();
		if (pos.isDrawable()) {
			float x = pos.getX().floatValue();
			float y = pos.getY().floatValue();
			float width = pos.getWidth().floatValue();

			if (isPinned.getValue()) {
				Render2D.drawRoundedBox(drawContext, x + width - 23, y + 8, 15, 15,
						GuiManager.roundingRadius.getValue(), new Color(154, 0, 0, 200));
				Render2D.drawRoundedBoxOutline(drawContext, x + width - 23, y + 8, 15, 15,
						GuiManager.roundingRadius.getValue(), new Color(0, 0, 0, 200));
			} else {
				Render2D.drawRoundedBox(drawContext, x + width - 23, y + 8, 15, 15,
						GuiManager.roundingRadius.getValue(), new Color(128, 128, 128, 50));
				Render2D.drawRoundedBoxOutline(drawContext, x + width - 23, y + 8, 15, 15,
						GuiManager.roundingRadius.getValue(), new Color(0, 0, 0, 50));
			}
		}
	}
}
