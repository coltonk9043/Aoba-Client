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
import net.aoba.gui.Margin;
import net.aoba.gui.Size;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

public class HudComponent extends Component {
	private String text;
	private HudWindow hud;

	public HudComponent(String text, HudWindow hud) {
		super();
		this.text = text;
		this.hud = hud;

		this.setMargin(new Margin(8f, 2f, 8f, 2f));
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

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();

		Render2D.drawString(drawContext, this.text, actualX, actualY + 8, 0xFFFFFF);

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
			if (this.hovered) {
				boolean visibility = hud.activated.getValue();
				Aoba.getInstance().guiManager.setHudActive(hud, !visibility);
			}
		}
	}
}