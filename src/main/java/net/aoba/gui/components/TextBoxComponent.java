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

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
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
import net.aoba.settings.types.StringSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public class TextBoxComponent extends Component implements KeyDownListener {
	private boolean listeningForKey;

	@Nullable
	private String text;
	private StringSetting stringSetting;

	private boolean isFocused = false;
	private float focusAnimationProgress = 0.0f;
	private Color errorBorderColor = new Color(255, 0, 0);
	private boolean isErrorState = false;

	// Events
	private Consumer<String> onTextChanged;

	public TextBoxComponent() {
		super();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.text = "";
	}

	public TextBoxComponent(String text) {
		super();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.text = text;
	}

	public TextBoxComponent(StringSetting stringSetting) {
		super();
		this.setMargin(new Margin(8f, 2f, 8f, 2f));

		this.stringSetting = stringSetting;
		this.stringSetting.addOnUpdate(s -> {
			this.text = s;
		});

		this.header = stringSetting.displayName;
		this.text = stringSetting.getValue();
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

		if (isFocused) {
			focusAnimationProgress = Math.min(1.0f, focusAnimationProgress + partialTicks * 0.1f);
		} else {
			focusAnimationProgress = Math.max(0.0f, focusAnimationProgress - partialTicks * 0.1f);
		}

		Render2D.drawOutlinedRoundedBox(matrix4f, actualX, actualY, actualWidth, actualHeight, 3.0f,
				GuiManager.borderColor.getValue(), new Color(115, 115, 115, 200));

		if (text != null && !text.isEmpty()) {
			int visibleStringLength = (int) (actualWidth - 16 / 10);

			int visibleStringIndex = Math.min(Math.max(0, text.length() - visibleStringLength - 1), text.length() - 1);
			String visibleString = text.substring(visibleStringIndex, text.length());
			Render2D.drawString(drawContext, visibleString, actualX + 8, actualY + 8, 0xFFFFFF);
		}
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

		isFocused = listeningForKey;
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (listeningForKey) {
			int key = event.GetKey();

			if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
				setListeningForKey(false);
			} else if (key == GLFW.GLFW_KEY_BACKSPACE) {
				if (!text.isEmpty()) {
					text = text.substring(0, text.length() - 1);
					if (stringSetting != null)
						stringSetting.setValue(text);
				}
			} else if (keyIsValid(key) || key == GLFW.GLFW_KEY_SPACE) {
				char keyCode = (char) key;

				if (key != GLFW.GLFW_KEY_SPACE && !Screen.hasShiftDown())
					keyCode = Character.toLowerCase(keyCode);

				text += "" + keyCode;
				if (stringSetting != null)
					stringSetting.setValue(text);
			}

			event.cancel();
		}
	}

	private boolean keyIsValid(int key) {
		return (key >= 48 && key <= 57) || (key >= 65 && key <= 90) || (key >= 97 && key <= 122);
	}

	public String getText() {
		return text;
	}

	public void setText(String newText) {
		text = newText;
		if (stringSetting != null)
			stringSetting.setValue(newText);
	}

	public void setErrorState(boolean isError) {
		this.isErrorState = isError;
	}

	private void setListeningForKey(boolean state) {
		listeningForKey = state;
		if (listeningForKey) {
			Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		} else {
			Aoba.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
			if (onTextChanged != null) {
				onTextChanged.accept(text);
			}
		}
	}

	public void setOnTextChanged(Consumer<String> onTextChanged) {
		this.onTextChanged = onTextChanged;
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		setListeningForKey(false);
	}
}
