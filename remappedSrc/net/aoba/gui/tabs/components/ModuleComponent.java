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
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.module.Module;
import net.aoba.gui.GuiManager;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.colors.Color;
import net.aoba.gui.tabs.ModuleSettingsTab;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class ModuleComponent extends Component implements LeftMouseDownListener {
	private String text;
	private Module module;

	private ModuleSettingsTab lastSettingsTab = null;
	
	public final Identifier gear;
	
	public ModuleComponent(String text, IGuiElement parent, Module module) {
		super(parent);
		
		gear = new Identifier("aoba", "/textures/gear.png");
		this.text = text;
		this.module = module;
		
		this.setLeft(2);
		this.setRight(2);
		this.setHeight(30);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
		RenderUtils.drawString(drawContext, this.text, actualX + 8, actualY + 8, module.getState() ? 0x00FF00 : this.hovered ? GuiManager.foregroundColor.getValue().getColorAsInt() : 0xFFFFFF);
		if(module.hasSettings()) {
			Color hudColor = GuiManager.foregroundColor.getValue();
			RenderUtils.drawTexturedQuad(drawContext, gear, (actualX + actualWidth - 20), (actualY + 6), 16, 16, hudColor);
		}
	}
	
	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		double mouseX = event.GetMouseX();
		if (hovered && Aoba.getInstance().hudManager.isClickGuiOpen()) {
				boolean isOnOptionsButton = (mouseX >= (actualX + actualWidth - 34) && mouseX <= (actualX + actualWidth));
				if (isOnOptionsButton) {
					if(lastSettingsTab == null) {
						lastSettingsTab = new ModuleSettingsTab(this.module.getName(), this.actualX + this.actualWidth + 1, this.actualY, this.module);
						lastSettingsTab.setVisible(true);
						Aoba.getInstance().hudManager.AddHud(lastSettingsTab, "Modules");
					}else {
						Aoba.getInstance().hudManager.RemoveHud(lastSettingsTab, "Modules");
						lastSettingsTab = null;
					}
				} else {
					module.toggle();
					return;
				}
		}
	}
	
	@Override
	public void OnVisibilityChanged() {
		if(this.isVisible()) {
			Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		}else {
			Aoba.getInstance().eventManager.RemoveListener(LeftMouseDownListener.class, this);
		}
	}
}
