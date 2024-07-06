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

import org.joml.Matrix4f;
import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.AbstractGui;
import net.aoba.gui.GuiManager;
import net.aoba.gui.colors.Color;
import net.aoba.gui.tabs.components.Component;
import net.aoba.misc.RenderUtils;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.aoba.utils.types.Vector2;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ClickGuiTab extends AbstractGui implements MouseClickListener, MouseMoveListener {
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
		SettingManager.registerSetting(isPinned, Aoba.getInstance().settingManager.hiddenContainer);
	}
	
	public ClickGuiTab(String title, int x, int y, boolean pinnable, String iconName) {
		super(title + "_tab", x, y, 180, 0);
		this.title = title;

		this.pinnable = pinnable;

		isPinned = new BooleanSetting(title + "_pinned", "IS PINNED", false);
		SettingManager.registerSetting(isPinned, Aoba.getInstance().settingManager.hiddenContainer);
		icon = Identifier.of("aoba", "/textures/" + iconName.trim().toLowerCase() + ".png");
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
	public void draw(DrawContext drawContext, float partialTicks) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		
		Vector2 pos = position.getValue();
		
		if (drawBorder) {
			// Draws background depending on components width and height
			RenderUtils.drawRoundedBox(matrix4f, pos.x, pos.y, width, height + 30, 6, GuiManager.backgroundColor.getValue());
			RenderUtils.drawRoundedOutline(matrix4f, pos.x, pos.y, width, height + 30, 6, GuiManager.borderColor.getValue());
			
			if(icon != null) {
				RenderUtils.drawTexturedQuad(matrix4f, icon, pos.x + 8, pos.y + 4, 22, 22, GuiManager.foregroundColor.getValue());
				RenderUtils.drawString(drawContext, this.title, pos.x + 38, pos.y + 8, GuiManager.foregroundColor.getValue());
			}else
				RenderUtils.drawString(drawContext, this.title, pos.x + 8, pos.y + 8, GuiManager.foregroundColor.getValue());
			
			RenderUtils.drawLine(matrix4f, pos.x, pos.y + 30, pos.x + width, pos.y + 30, new Color(0, 0, 0, 100));

			if (this.pinnable) {
				if (this.isPinned.getValue()) {
					RenderUtils.drawRoundedBox(matrix4f, pos.x + width - 23, pos.y + 8, 15, 15, 6f, new Color(154, 0, 0, 200));
					RenderUtils.drawRoundedOutline(matrix4f, pos.x + width - 23, pos.y + 8, 15, 15, 6f, new Color(0, 0, 0, 200));
				} else {
					RenderUtils.drawRoundedBox(matrix4f, pos.x + width - 23, pos.y + 8, 15, 15, 6f, new Color(128, 128, 128, 50));
					RenderUtils.drawRoundedOutline(matrix4f, pos.x + width - 23, pos.y + 8, 15, 15, 6f, new Color(0, 0, 0, 50));
				}
			}
		}
		for (Component child : children) {
			child.draw(drawContext, partialTicks);
		}
	}
	
	@Override
	public void OnMouseClick(MouseClickEvent event) {
		if(event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
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
}
