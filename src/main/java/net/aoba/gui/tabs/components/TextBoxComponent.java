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

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.event.listeners.MouseClickListener;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.colors.Color;
import net.aoba.misc.RenderUtils;
import net.aoba.settings.types.StringSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class TextBoxComponent extends Component implements MouseClickListener, KeyDownListener {
	private boolean listeningForKey;
	private StringSetting string;

	public TextBoxComponent(IGuiElement parent, StringSetting stringSetting) {
		super(parent);
		this.string = stringSetting;
		
		this.setHeight(30);
		
		Aoba.getInstance().eventManager.AddListener(MouseClickListener.class, this);
		Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
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
	public void update() {
		super.update();
	}
	
	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);
		
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		
		RenderUtils.drawString(drawContext, string.displayName, actualX + 8, actualY + 8, 0xFFFFFF);
		RenderUtils.drawBox(matrix4f, actualX + actualWidth - 150, actualY + 2, 143, actualHeight - 4, new Color(115, 115, 115, 200));
		RenderUtils.drawOutline(matrix4f, actualX + actualWidth - 150, actualY + 2, 143, actualHeight - 4);
		
		String keyBindText = this.string.getValue();
		if(!keyBindText.isEmpty()) {
			int visibleStringLength = 120 / 10;
			String visibleString = keyBindText.substring(Math.max(0, keyBindText.length() - visibleStringLength - 1), keyBindText.length());
			RenderUtils.drawString(drawContext, visibleString, actualX + actualWidth - 145, actualY + 8, 0xFFFFFF);
		}
	}

	@Override
	public void OnMouseClick(MouseClickEvent event) {
		if(event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if(Aoba.getInstance().hudManager.isClickGuiOpen()) {
				if (hovered) {
					listeningForKey = true;
				}else {
					listeningForKey = false;
				}
			}
		}
	}
	
	@Override
	public void OnKeyDown(KeyDownEvent event) {
		if(listeningForKey) {
			int key = event.GetKey();
			
			if(key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
				listeningForKey = false;
			}else if(key == GLFW.GLFW_KEY_BACKSPACE) {
				String currentVal = string.getValue();
				if(currentVal.length() > 0)
					string.setValue(currentVal.substring(0, currentVal.length() - 1));
			}else {
				String currentVal = string.getValue();
				currentVal += "" + (char)key;
				string.setValue(currentVal);
			}
			
			event.cancel();
		}
	}

}
