/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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

import java.util.ArrayList;
import java.util.function.Consumer;

import net.aoba.Aoba;
import net.aoba.core.settings.SettingManager;
import net.aoba.core.settings.osettingtypes.BooleanSetting;
import net.aoba.core.settings.osettingtypes.Vector2Setting;
import net.aoba.core.utils.types.Vector2;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.hud.AbstractHud;
import net.aoba.gui.tabs.components.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;


public class ClickGuiTab extends AbstractHud{
	protected String title;

	protected boolean pinnable = false;
	protected boolean isPinned = false;
	protected boolean pinWasClicked = false;
	protected boolean drawBorder = true;
	protected boolean inheritHeightFromChildren = true;
	
	protected ArrayList<Component> children = new ArrayList<>();

	private Consumer<Vector2> update_pos;
	private Vector2Setting position;
	private Consumer<Boolean> update_pinned;
	private BooleanSetting pinned_setting;

	public ClickGuiTab(String title, int x, int y, boolean pinnable) {
		super(title + "_tab", x, y, 180, 0);
		this.title = title;
		this.x = x;
		this.y = y;
		this.width = 180;
		this.mc = MinecraftClient.getInstance();
		this.pinnable = pinnable;

		update_pos = new Consumer<Vector2>() {
			@Override
			public void accept(Vector2 vector2) {
				setX((int)vector2.x);
				setY((int)vector2.y);
			}
		};

		update_pinned = new Consumer<Boolean>() {
			@Override
			public void accept(Boolean aBoolean) {
				isPinned = aBoolean;
			}
		};

		position = new Vector2Setting(title + "_position", "GUI POS", new Vector2(x, y), update_pos);
		pinned_setting = new BooleanSetting(title + "_pinned", "IS PINNED", false, update_pinned);
		SettingManager.register_setting(position, Aoba.getInstance().settingManager.hidden_category);
		SettingManager.register_setting(pinned_setting, Aoba.getInstance().settingManager.hidden_category);
	}


	public final String getTitle() {
		return title;
	}

	public final boolean isPinned() {
		return this.isPinned;
	}

	public final void setPinned(boolean pin) {
		this.isPinned = pin;
	}

	public final boolean getPinClicked() {
		return this.pinWasClicked;
	}

	public final void setPinClicked(boolean pin) {
		this.pinWasClicked = pin;
	}

	public final void setTitle(String title) {

		this.title = title;
	}

	public final int getX() {
		return x;
	}

	public final void setX(int x) {
		this.x = x;
	}

	public final int getY() {
		return y;
	}

	public final void setY(int y) {
		this.y = y;
	}

	public final int getWidth() {
		return width;
	}

	public final void setWidth(int width) {
		this.width = width;
	}

	public final int getHeight() {
		return height;
	}

	public final void setHeight(int height) {
		this.height = height;
	}

	public final boolean getPinned() {
		return this.isPinned;
	}

	public final boolean isGrabbed() {
		return (HudManager.currentGrabbed == this);
	}

	public final void addChild(Component component) {
		this.children.add(component);
	}

	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		if(this.inheritHeightFromChildren) {
			int tempHeight = 1;
			for (Component child : children) {
				tempHeight += (child.getHeight());
			}
			this.height = tempHeight;
		}
		
		if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= (x) && mouseX <= (x + width)) {
					if (mouseY >= (y) && mouseY <= (y + 28)) {
						if (mouseClicked) {
							boolean isInsidePinButton = false;
							if(this.pinnable) {
								if (mouseX >= (x + width - 24) && mouseX <= (x + width - 2)) {
									if (mouseY >= (y + 4) && mouseY <= (y + 20)) {
										isInsidePinButton = true;
									}
								}
							}
							if (isInsidePinButton) {
								if (!this.pinWasClicked) {
									this.isPinned = !this.isPinned;
									this.pinWasClicked = true;
									return;
								}
							} else {
								if (!this.isPinned) HudManager.currentGrabbed = this;
							}
						} else {
							if (this.pinWasClicked) {
								this.pinWasClicked = false;
							}
							if (!this.isPinned) HudManager.currentGrabbed = this;
						}
					}
				}
			}

			int i = 30;
			for (Component child : this.children) {
				child.update(i, mouseX, mouseY, mouseClicked);
				i += child.getHeight();
			}
		}

		position.silentSetX(x);
		position.silentSetY(y);
		pinned_setting.silentSetValue(isPinned);
	}

	public void preupdate() {
	}

	public void postupdate() {
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();
		if(drawBorder) {
			// Draws background depending on components width and height
			renderUtils.drawRoundedBox(matrixStack, x, y, width, height + 30, 6, new Color(30,30,30), 0.4f);
			renderUtils.drawRoundedOutline(matrixStack, x, y, width, height + 30, 6, new Color(0,0,0), 0.8f);
			renderUtils.drawString(drawContext, this.title, x + 8, y + 8, Aoba.getInstance().hudManager.getColor());
			renderUtils.drawLine(matrixStack, x, y + 30, x + width, y + 30, new Color(0,0,0), 0.4f);

			if(this.pinnable) {
				if (this.isPinned) {
					renderUtils.drawRoundedBox(matrixStack, x + width - 23, y + 8, 15, 15, 6f, new Color(154,0,0), 0.8f);
					renderUtils.drawRoundedOutline(matrixStack, x + width - 23, y + 8, 15, 15, 6f, new Color(0,0,0), 0.8f);
				} else {
					renderUtils.drawRoundedBox(matrixStack, x + width - 23, y + 8, 15, 15, 6f, new Color(128,128,128), 0.2f);
					renderUtils.drawRoundedOutline(matrixStack, x + width - 23, y + 8, 15, 15, 6f, new Color(0,0,0), 0.2f);
				}
			}
		}
		int i = 30;
		for (Component child : children) {
			child.draw(i, drawContext, partialTicks, color);
			i += child.getHeight();
		}
	}
}
