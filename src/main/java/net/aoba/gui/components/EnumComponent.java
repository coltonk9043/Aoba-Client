package net.aoba.gui.components;

import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.listeners.KeyDownListener;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.EnumSetting;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class EnumComponent<T extends Enum<T>> extends Component implements KeyDownListener {
	private boolean listeningForKey;
	private EnumSetting<T> enumSetting;
	private boolean isFocused = false;
	private float focusAnimationProgress = 0.0f;
	private Color focusBorderColor = new Color(255, 255, 255);
	private Color errorBorderColor = new Color(255, 0, 0);
	private boolean isErrorState = false;

	public EnumComponent(IGuiElement parent, EnumSetting<T> enumSetting) {
		super(parent, new Rectangle(null, null, null, 30f));
		this.enumSetting = enumSetting;

		this.setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		
		if (this.isVisible())
			Aoba.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		else
			Aoba.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
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

		float actualX = actualSize.getX();
		float actualY = actualSize.getY();
		float actualWidth = actualSize.getWidth();
		float actualHeight = actualSize.getHeight();

		if (isFocused) {
			focusAnimationProgress = Math.min(1.0f, focusAnimationProgress + partialTicks * 0.1f);
		} else {
			focusAnimationProgress = Math.max(0.0f, focusAnimationProgress - partialTicks * 0.1f);
		}

		Color borderColor = isErrorState ? errorBorderColor
				: new Color(115 + (int) (140 * focusAnimationProgress), 115, 115, 200);

		Render2D.drawString(drawContext, enumSetting.displayName, actualX, actualY + 8, 0xFFFFFF);
		
		// Right Text
		Render2D.drawOutlinedRoundedBox(matrix4f, actualX + actualWidth - 150, actualY, 150, actualHeight, 3.0f, borderColor, new Color(115, 115, 115, 200));

		String enumValue = this.enumSetting.getValue().toString();
		Render2D.drawString(drawContext, enumValue, actualX + actualWidth - 145, actualY + 8, 0xFFFFFF);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				listeningForKey = true;
				event.cancel();
			} else {
				listeningForKey = false;
			}
		}

		isFocused = listeningForKey;
	}

	@Override
	public void OnKeyDown(KeyDownEvent event) {
		if (listeningForKey) {
			int key = event.GetKey();

			if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
				listeningForKey = false;
			} else {
				T currentValue = enumSetting.getValue();
				T[] enumConstants = currentValue.getDeclaringClass().getEnumConstants();
				int currentIndex = java.util.Arrays.asList(enumConstants).indexOf(currentValue);
				int enumCount = enumConstants.length;

				if (key == GLFW.GLFW_KEY_RIGHT) {
					currentIndex = (currentIndex + 1) % enumCount;
				} else if (key == GLFW.GLFW_KEY_LEFT) {
					currentIndex = (currentIndex - 1 + enumCount) % enumCount;
				}

				enumSetting.setValue(enumConstants[currentIndex]);
			}

			event.cancel();
		}
	}

	public void setErrorState(boolean isError) {
		this.isErrorState = isError;
	}
}
