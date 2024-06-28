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

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.hud.AbstractHud;
import net.aoba.misc.RenderUtils;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

public class HudComponent extends Component implements MouseClickListener {
	private String text;
	private AbstractHud hud;

	public HudComponent(String text, IGuiElement parent, AbstractHud hud) {
		super(parent);
		this.text = text;
		this.hud = hud;
		
		this.setHeight(30);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
		RenderUtils.drawString(drawContext, this.text, actualX + 8, actualY + 8, 0xFFFFFF);
		if(this.hud.activated.getValue()) {
			RenderUtils.drawString(drawContext, "-", actualX + actualWidth - 16, actualY + 8, 0xFF0000);
		}else {
			RenderUtils.drawString(drawContext, "+", actualX + actualWidth - 16, actualY + 8, 0x00FF00);
		}
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
			if(this.hovered && Aoba.getInstance().hudManager.isClickGuiOpen()) {
				boolean visibility = hud.activated.getValue();
				Aoba.getInstance().hudManager.SetHudActive(hud, !visibility);
			}
		}
	}
}