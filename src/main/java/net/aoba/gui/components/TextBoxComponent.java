package net.aoba.gui.components;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
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

public class TextBoxComponent extends Component implements KeyDownListener {
	private boolean listeningForKey;

	@Nullable
	private String text;
	private StringSetting stringSetting;

	private boolean isFocused = false;
	private float focusAnimationProgress = 0.0f;
	private final Color errorBorderColor = new Color(255, 0, 0);
	private boolean isErrorState = false;

	// Events
	private Consumer<String> onTextChanged;

	public TextBoxComponent() {
		setMargin(new Margin(8f, 2f, 8f, 2f));
		text = "";
	}

	public TextBoxComponent(String text) {
		setMargin(new Margin(8f, 2f, 8f, 2f));
		this.text = text;
	}

	public TextBoxComponent(StringSetting stringSetting) {
		setMargin(new Margin(8f, 2f, 8f, 2f));

		this.stringSetting = stringSetting;
		this.stringSetting.addOnUpdate(s -> {
			text = s;
		});

		header = stringSetting.displayName;
		text = stringSetting.getValue();
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

		if (isFocused) {
			focusAnimationProgress = Math.min(1.0f, focusAnimationProgress + partialTicks * 0.1f);
		} else {
			focusAnimationProgress = Math.max(0.0f, focusAnimationProgress - partialTicks * 0.1f);
		}

		Color borderColor = isErrorState ? errorBorderColor : GuiManager.borderColor.getValue();
		Render2D.drawOutlinedRoundedBox(drawContext, actualX, actualY, actualWidth, actualHeight, 3.0f, borderColor,
				new Color(115, 115, 115, 200));

		if (text != null && !text.isEmpty()) {
			int visibleStringLength = (int) (actualWidth - 16 / 10);

			int visibleStringIndex = Math.min(Math.max(0, text.length() - visibleStringLength - 1), text.length() - 1);
			String visibleString = text.substring(visibleStringIndex);
			Render2D.drawString(drawContext, visibleString, actualX + 8, actualY + 8, 0xFFFFFF);
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				if (!listeningForKey) {
					setListeningForKey(true);
				}
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
				String keyName = GLFW.glfwGetKeyName(key, event.GetScanCode());
				if (keyName != null && !keyName.isEmpty()) {
					char keyCode = keyName.charAt(0);

					if (key != GLFW.GLFW_KEY_SPACE && !Screen.hasShiftDown())
						keyCode = Character.toLowerCase(keyCode);

					text += keyCode;
					if (stringSetting != null)
						stringSetting.setValue(text);
				}
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
		isErrorState = isError;
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