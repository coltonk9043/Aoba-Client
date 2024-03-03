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

/**
 * A class to represent a ClickGui Tab that contains different Components.
 */

package net.aoba.gui.tabs;

import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.AbstractGui;
import net.aoba.gui.Color;
import net.aoba.gui.GuiManager;
import net.aoba.gui.tabs.components.Component;
import net.aoba.misc.RenderUtils;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.types.Vector2;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ClickGuiTab extends AbstractGui implements LeftMouseDownListener, MouseMoveListener {
	protected String title;

	protected boolean pinnable = true;
	protected boolean drawBorder = true;

	private BooleanSetting isPinned;
	private Identifier icon = null;
	
	public ClickGuiTab(String title, int x, int y, boolean pinnable) {
		super(title + "_tab", x, y, 180, 0);
		this.title = title;

		this.pinnable = pinnable;

		isPinned = new BooleanSetting(title + "_pinned", "IS PINNED", false);
		SettingManager.registerSetting(isPinned, Aoba.getInstance().settingManager.hidden_category);
	}
	
	public ClickGuiTab(String title, int x, int y, boolean pinnable, String iconName) {
		super(title + "_tab", x, y, 180, 0);
		this.title = title;

		this.pinnable = pinnable;

		isPinned = new BooleanSetting(title + "_pinned", "IS PINNED", false);
		SettingManager.registerSetting(isPinned, Aoba.getInstance().settingManager.hidden_category);
		icon = new Identifier("aoba", "/textures/" + iconName.trim().toLowerCase() + ".png");
	}

	public final String getTitle() {
		return title;
	}

	public final boolean isPinned() {
		return isPinned.getValue();
	}

	public final void setPinned(boolean pin) {
		this.isPinned.setValue(pin);
	}

	public final void setTitle(String title) {

		this.title = title;
	}

	public final boolean isGrabbed() {
		return (GuiManager.currentGrabbed == this);
	}

	public final void addChild(Component component) {
		this.children.add(component);
	}

	@Override
	public void update() {
		if (this.inheritHeightFromChildren) {
			float tempHeight = 0;
			for (Component child : children) {
				tempHeight += (child.getHeight());
			}
			this.setHeight(tempHeight);
		}
		
		if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
			for (Component child : this.children) {
				child.update();
			}
		}
	}

	public void preupdate() {
	}

	public void postupdate() {
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();
		
		Vector2 pos = position.getValue();
		
		if (drawBorder) {
			// Draws background depending on components width and height
			RenderUtils.drawRoundedBox(matrixStack, pos.x, pos.y, width, height + 30, 6, Aoba.getInstance().hudManager.backgroundColor.getValue());
			RenderUtils.drawRoundedOutline(matrixStack, pos.x, pos.y, width, height + 30, 6, Aoba.getInstance().hudManager.borderColor.getValue());
			
			if(icon != null) {
				RenderUtils.drawTexturedQuad(drawContext, icon, pos.x + 8, pos.y + 4, 22, 22, color);
				RenderUtils.drawString(drawContext, this.title, pos.x + 38, pos.y + 8, Aoba.getInstance().hudManager.getColor());
			}else
				RenderUtils.drawString(drawContext, this.title, pos.x + 8, pos.y + 8, Aoba.getInstance().hudManager.getColor());
			
			RenderUtils.drawLine(matrixStack, pos.x, pos.y + 30, pos.x + width, pos.y + 30, new Color(0, 0, 0, 100));

			if (this.pinnable) {
				if (this.isPinned.getValue()) {
					RenderUtils.drawRoundedBox(matrixStack, pos.x + width - 23, pos.y + 8, 15, 15, 6f, new Color(154, 0, 0, 200));
					RenderUtils.drawRoundedOutline(matrixStack, pos.x + width - 23, pos.y + 8, 15, 15, 6f, new Color(0, 0, 0, 200));
				} else {
					RenderUtils.drawRoundedBox(matrixStack, pos.x + width - 23, pos.y + 8, 15, 15, 6f, new Color(128, 128, 128, 50));
					RenderUtils.drawRoundedOutline(matrixStack, pos.x + width - 23, pos.y + 8, 15, 15, 6f, new Color(0, 0, 0, 50));
				}
			}
		}
		for (Component child : children) {
			child.draw(drawContext, partialTicks, color);
		}
	}

	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		double mouseX = mc.mouse.getX();
		double mouseY = mc.mouse.getY();
		Vector2 pos = position.getValue();
		
		if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
			// Allow the user to move the clickgui if it within the header bar and NOT pinned.
			if(!isPinned.getValue()) {
				if(mouseX >= pos.x && mouseX <= pos.x + width) {
					if(mouseY >= pos.y && mouseY <= pos.y + 24) {
						lastClickOffsetX = mouseX - pos.x;
						lastClickOffsetY = mouseY - pos.y;
						GuiManager.currentGrabbed = this;
					}
				}
			}
			
			// If the GUI is pinnable, allow the user to click the pin button to pin a gui
			if (pinnable) {
				if (mouseX >= (pos.x + width - 24) && mouseX <= (pos.x + width - 2)) {
					if (mouseY >= (pos.y + 4) && mouseY <= (pos.y + 20)) {
						GuiManager.currentGrabbed = null;
						isPinned.silentSetValue(!isPinned.getValue());
					}
				}
			}
		}
	}
}
