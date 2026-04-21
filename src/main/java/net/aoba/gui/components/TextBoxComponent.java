package net.aoba.gui.components;

import java.util.function.Consumer;
import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.types.TextWrapping;
import net.aoba.gui.types.Thickness;
import net.aoba.gui.types.VerticalAlignment;
import net.aoba.utils.input.CursorStyle;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class TextBoxComponent extends Component implements KeyDownListener {
	private boolean listeningForKey;

	private static final Shader CARET_SHADER = Shader.solid(Colors.White);
	public static UIProperty<String> HeaderProperty = new UIProperty<String>("Header", "", false, true);
	public static UIProperty<String> TextProperty = new UIProperty<String>("Text", "", false, true,
			TextBoxComponent::onTextPropertyChanged);
	public static UIProperty<String> PlaceholderText = new UIProperty<String>("PlaceholderText", "", false, true);

	private boolean isFocused = false;
	private int caretTick = 0;
	private boolean caretVisible = true;

	private final RectangleComponent box;
	private final StringComponent textComponent;

	private Consumer<String> onTextChanged;

	private static void onTextPropertyChanged(UIElement sender, String oldValue, String newValue) {
		if (sender instanceof TextBoxComponent textBoxComponent) {
			textBoxComponent.textComponent.setProperty(StringComponent.TextProperty, newValue);
			if (textBoxComponent.onTextChanged != null)
				textBoxComponent.onTextChanged.accept(newValue);
		}
	}

	public TextBoxComponent() {
		setProperty(UIElement.HeightProperty, 30.0f);
		setProperty(UIElement.CursorProperty, CursorStyle.Type);
		
		box = new RectangleComponent();
		box.bindProperty(UIElement.BackgroundProperty, GuiManager.componentBackgroundColor);
		box.bindProperty(UIElement.BorderProperty, GuiManager.componentBorderColor);
		box.bindProperty(UIElement.CornerRadiusProperty, GuiManager.roundingRadius);
		box.setProperty(UIElement.PaddingProperty, new Thickness(4f));

		textComponent = new StringComponent(getProperty(TextProperty));
		textComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		textComponent.setProperty(StringComponent.TextWrappingProperty, TextWrapping.NoWrap);
		textComponent.setProperty(UIElement.IsHitTestVisibleProperty, false);
		box.setContent(textComponent);

		setContent(box);

		setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				if (!listeningForKey) {
					setListeningForKey(true);
				}
				e.cancel();
			}
		});
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
	public void draw(Renderer2D renderer, float partialTicks) {
		super.draw(renderer, partialTicks);

		if (isFocused && caretVisible) {
			float textX = textComponent.getActualSize().x();
			float textY = textComponent.getActualSize().y();
			float textWidth = textComponent.getPreferredSize().width();
			float textHeight = textComponent.getPreferredSize().height();

			float caretX = textX + textWidth;
			renderer.drawBox(caretX, textY, 2, textHeight, CARET_SHADER);
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			boolean hovered = getProperty(UIElement.IsHoveredProperty);
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
			String currentText = getProperty(TextProperty);

			if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
				setListeningForKey(false);
			} else if (key == GLFW.GLFW_KEY_BACKSPACE) {
				if (!currentText.isEmpty()) {
					setProperty(TextProperty, currentText.substring(0, currentText.length() - 1));
				}
			} else if (key == GLFW.GLFW_KEY_SPACE) {
				setProperty(TextProperty, currentText + ' ');
			} else if (keyIsValid(key)) {
				String keyName = GLFW.glfwGetKeyName(key, event.GetScanCode());
				if (keyName != null && !keyName.isEmpty()) {
					char keyCode = keyName.charAt(0);

					boolean shiftDown = GLFW.glfwGetKey(AobaClient.MC.getWindow().handle(),
							GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
							|| GLFW.glfwGetKey(AobaClient.MC.getWindow().handle(),
									GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
					if (!shiftDown)
						keyCode = Character.toLowerCase(keyCode);

					setProperty(TextProperty, currentText + keyCode);
				}
			}

			event.cancel();
		}
	}

	private boolean keyIsValid(int key) {
		return key == 45 || (key >= 48 && key <= 57) || (key >= 65 && key <= 90) || (key >= 97 && key <= 122);
	}

	private void setListeningForKey(boolean state) {
		if (listeningForKey == state)
			return;

		listeningForKey = state;
		isFocused = state;
		if (listeningForKey) {
			GuiManager.requestFocus(this);
			caretVisible = true;
			caretTick = 0;
			Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		} else {
			GuiManager.clearFocus(this);
			Aoba.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
		}
	}

	@Override
	protected void onLostFocus() {
		if (listeningForKey) {
			listeningForKey = false;
			isFocused = false;
			Aoba.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
		}
	}

	public void setOnTextChanged(Consumer<String> onTextChanged) {
		this.onTextChanged = onTextChanged;
	}

	@Override
	protected void onVisibilityChanged(Boolean oldValue, Boolean newValue) {
		super.onVisibilityChanged(oldValue, newValue);
		if (!newValue)
			setListeningForKey(false);
	}
}
