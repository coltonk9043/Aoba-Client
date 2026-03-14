package net.aoba.gui.components;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.TextWrapping;
import net.aoba.gui.Thickness;
import net.aoba.gui.VerticalAlignment;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.aoba.settings.types.StringSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.GuiGraphics;

public class TextBoxComponent extends Component implements KeyDownListener {
	private static final Color BACKGROUND_COLOR = new Color(115, 115, 115, 200);
	private static final Color ERROR_BORDER_COLOR = new Color(255, 0, 0);

	private boolean listeningForKey;

	@Nullable
	private String text;
	private StringSetting stringSetting;

	private boolean isFocused = false;
	private boolean isErrorState = false;
	private int caretTick = 0;
	private boolean caretVisible = true;

	private Consumer<String> onTextChanged;

	private final RectangleComponent box;
	private final StringComponent textComponent;

	private TextBoxComponent(String text) {
		this.text = text != null ? text : "";

		setHeight(30.0f);

		box = new RectangleComponent(
				BACKGROUND_COLOR,
				GuiManager.borderColor.getValue(),
				3f);
		box.setPadding(new Thickness(4f));

		textComponent = new StringComponent(this.text);
		textComponent.setVerticalAlignment(VerticalAlignment.Center);
		textComponent.setTextWrapping(TextWrapping.NoWrap);
		textComponent.setIsHitTestVisible(false);
		box.addChild(textComponent);

		addChild(box);

		setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				if (!listeningForKey) {
					setListeningForKey(true);
				}
				e.cancel();
			}
		});
	}

	public TextBoxComponent() {
		this("");
	}

	public TextBoxComponent(StringSetting stringSetting) {
		this(stringSetting.getValue());
		this.stringSetting = stringSetting;
		this.stringSetting.addOnUpdate(this::onSettingValueChanged);
		header = stringSetting.displayName;
	}

	@Override
	public void update() {
		super.update();
		if (isFocused) {
			caretTick++;
			if (caretTick >= 10) {
				caretVisible = !caretVisible;
				caretTick = 0;
			}
		} else {
			caretVisible = false;
			caretTick = 0;
		}
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		if (isFocused && caretVisible) {
			float textX = textComponent.getActualSize().getX();
			float textY = textComponent.getActualSize().getY();
			float textWidth = textComponent.getPreferredSize().getWidth();
			float textHeight = textComponent.getPreferredSize().getHeight();

			float caretX = textX + textWidth;
			Render2D.drawBox(drawContext, caretX, textY, 2, textHeight, Colors.White);
		}
	}

	private void onSettingValueChanged(String s) {
		if (!s.equals(text)) {
			text = s;
			textComponent.setText(text);
		}
	}

	private void updateBorderColor() {
		if (isErrorState) {
			box.setBorderColor(ERROR_BORDER_COLOR);
		} else {
			box.setBorderColor(GuiManager.borderColor.getValue());
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (!hovered && listeningForKey) {
				setListeningForKey(false);
			}
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (listeningForKey) {
			caretVisible = true;
			caretTick = 0;

			int key = event.GetKey();

			if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
				setListeningForKey(false);
			} else if (key == GLFW.GLFW_KEY_BACKSPACE) {
				if (text != null && !text.isEmpty()) {
					text = text.substring(0, text.length() - 1);
					textComponent.setText(text);
					if (stringSetting != null)
						stringSetting.setValue(text);
				}
			} else if (keyIsValid(key) || key == GLFW.GLFW_KEY_SPACE) {
				String keyName = GLFW.glfwGetKeyName(key, event.GetScanCode());
				if (keyName != null && !keyName.isEmpty()) {
					char keyCode = keyName.charAt(0);

					boolean shiftDown = GLFW.glfwGetKey(net.aoba.AobaClient.MC.getWindow().handle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
							|| GLFW.glfwGetKey(net.aoba.AobaClient.MC.getWindow().handle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
					if (key != GLFW.GLFW_KEY_SPACE && !shiftDown)
						keyCode = Character.toLowerCase(keyCode);

					text += keyCode;
					textComponent.setText(text);
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
		textComponent.setText(text);
		if (stringSetting != null)
			stringSetting.setValue(newText);
	}

	public void setErrorState(boolean isError) {
		isErrorState = isError;
		updateBorderColor();
	}

	private void setListeningForKey(boolean state) {
		listeningForKey = state;
		isFocused = state;
		GuiManager.setKeyboardInputActive(state);
		if (listeningForKey) {
			caretVisible = true;
			caretTick = 0;
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
