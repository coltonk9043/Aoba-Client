/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

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

public class KeybindComponent extends Component implements KeyDownListener {
	private boolean listeningForKey;
	private final KeybindSetting keyBind;

	public KeybindComponent(KeybindSetting keyBind) {
		setMargin(new Margin(8f, 2f, 8f, 2f));
		this.keyBind = keyBind;
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (isVisible()) {
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

		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();
		float actualHeight = getActualSize().getHeight();

		Render2D.drawString(drawContext, "Keybind", actualX, actualY + 8, 0xFFFFFF);
		Render2D.drawOutlinedRoundedBox(drawContext, actualX + actualWidth - 100, actualY, 100, actualHeight, 3.0f,
				GuiManager.borderColor.getValue(), new Color(115, 115, 115, 200));

		String keyBindText = keyBind.getValue().getLocalizedText().getString();
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