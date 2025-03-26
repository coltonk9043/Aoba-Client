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
import net.aoba.gui.Margin;
import net.aoba.gui.Size;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

public class HudComponent extends Component {
	private final String text;
	private final HudWindow hud;

	public HudComponent(String text, HudWindow hud) {
        this.text = text;
		this.hud = hud;

		setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	@Override
	public void measure(Size availableSize) {
		preferredSize = new Size(availableSize.getWidth(), 30.0f);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();

		Render2D.drawString(drawContext, text, actualX, actualY + 8, 0xFFFFFF);

		if (hud.activated.getValue()) {
			Render2D.drawString(drawContext, "-", actualX + actualWidth - 12, actualY + 8, 0xFF0000);
		} else {
			Render2D.drawString(drawContext, "+", actualX + actualWidth - 12, actualY + 8, 0x00FF00);
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				boolean visibility = hud.activated.getValue();
				Aoba.getInstance().guiManager.setHudActive(hud, !visibility);
			}
		}
	}
}