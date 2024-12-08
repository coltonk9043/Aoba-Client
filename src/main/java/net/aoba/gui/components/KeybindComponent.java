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

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Size;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.KeybindSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;

public class KeybindComponent extends Component implements KeyDownListener {
	private boolean listeningForKey;
	private KeybindSetting keyBind;

	public KeybindComponent(KeybindSetting keyBind) {
		super();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.keyBind = keyBind;
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (this.isVisible()) {
			Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		} else {
			Aoba.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
		}
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

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();
		float actualHeight = this.getActualSize().getHeight();

		Render2D.drawString(drawContext, "Keybind", actualX, actualY + 8, 0xFFFFFF);
		Render2D.drawOutlinedRoundedBox(matrix4f, actualX + actualWidth - 100, actualY, 100, actualHeight, 3.0f,
				GuiManager.borderColor.getValue(), new Color(115, 115, 115, 200));

		String keyBindText = this.keyBind.getValue().getLocalizedText().getString();
		if (keyBindText.equals("scancode.0") || keyBindText.equals("key.keyboard.0"))
			keyBindText = "N/A";

		Render2D.drawString(drawContext, keyBindText, actualX + actualWidth - 90, actualY + 6, 0xFFFFFF);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				setListeningForKey(true);
				event.cancel();
			} else {
				setListeningForKey(false);
			}
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (listeningForKey) {
			int key = event.GetKey();
			int scanCode = event.GetScanCode();

			if (key == GLFW.GLFW_KEY_ESCAPE) {
				keyBind.setValue(InputUtil.UNKNOWN_KEY);
			} else {
				keyBind.setValue(InputUtil.fromKeyCode(key, scanCode));
			}

			listeningForKey = false;

			event.cancel();
		}
	}

	private void setListeningForKey(boolean state) {
		listeningForKey = state;
		if (listeningForKey) {
			Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		} else {
			Aoba.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
		}
	}
}