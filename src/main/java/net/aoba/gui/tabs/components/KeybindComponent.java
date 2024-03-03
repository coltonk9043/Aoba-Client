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

import org.lwjgl.glfw.GLFW;

import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.gui.Color;
import net.aoba.gui.IGuiElement;
import net.aoba.misc.RenderUtils;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;

public class KeybindComponent extends Component implements LeftMouseDownListener, KeyDownListener {
	private boolean listeningForKey;
	private KeybindSetting keyBind;
	
	public KeybindComponent(IGuiElement parent, KeybindSetting keyBind) {
		super(parent);
		this.keyBind = keyBind;
		
		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		super.draw(drawContext, partialTicks, color);
		RenderUtils.drawString(drawContext, "Keybind", actualX + 8, actualY + 8, 0xFFFFFF);
		RenderUtils.drawBox(drawContext.getMatrices(), actualX + actualWidth - 100, actualY + 2, 98, actualHeight - 4, new Color(115, 115, 115, 200));
		RenderUtils.drawOutline(drawContext.getMatrices(), actualX + actualWidth - 100, actualY + 2, 98, actualHeight - 4);
		
		String keyBindText = this.keyBind.getValue().getLocalizedText().getString();
		if(keyBindText.equals("scancode.0") || keyBindText.equals("key.keyboard.0"))
			keyBindText = "N/A";
		
		RenderUtils.drawString(drawContext, keyBindText, actualX + actualWidth - 90, actualY + 8, 0xFFFFFF);
	}

	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		if (hovered && Aoba.getInstance().hudManager.isClickGuiOpen()) {
			listeningForKey = !listeningForKey;
		}
	}

	@Override
	public void OnKeyDown(KeyDownEvent event) {
		if(listeningForKey) {
			int key = event.GetKey();
			int scanCode = event.GetScanCode();
			
			if(key == GLFW.GLFW_KEY_ESCAPE) {
				keyBind.setValue(InputUtil.UNKNOWN_KEY);
			}else {
				keyBind.setValue(InputUtil.fromKeyCode(key, scanCode));
			}
			
			listeningForKey = false;
			
			event.SetCancelled(true);
		}
	}
}