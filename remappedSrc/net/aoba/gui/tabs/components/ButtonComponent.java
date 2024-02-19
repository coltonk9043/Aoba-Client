package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.gui.Color;
import net.aoba.gui.IGuiElement;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ButtonComponent extends Component implements LeftMouseDownListener {

	private String text;
	private Runnable onClick;
	private Color color = new Color(128, 128, 128);
	private Color backgroundColor = color;
	
	/**
	 * Constructor for button component.
	 * @param parent Parent Tab that this Component resides in.
	 * @param text Text contained in this button element.
	 * @param onClick OnClick delegate that will run when the button is pressed.
	 */
	public ButtonComponent(IGuiElement parent, String text, Runnable onClick) {
		super(parent);
		
		this.setLeft(2);
		this.setRight(2);
		this.setHeight(30);
		
		this.text = text;
		this.onClick = onClick;
	}

	/**
	 * Sets the text of the button.
	 * @param text Text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Sets the OnClick delegate of the button.
	 * @param onClick Delegate to set.
	 */
	public void setOnClick(Runnable onClick) {
		this.onClick = onClick;
	}

	/**
	 * Draws the button to the screen.
	 * @param offset The offset (Y location relative to parent) of the Component.
	 * @param drawContext The current draw context of the game.
	 * @param partialTicks The partial ticks used for interpolation.
	 * @param color The current Color of the UI.
	 */
	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();
		RenderUtils.drawOutlinedBox(matrixStack, actualX + 2, actualY, actualWidth - 4, actualHeight - 2, backgroundColor);
		RenderUtils.drawString(drawContext, this.text, actualX + 8, actualY + 8, 0xFFFFFF);
	}

	/**
	 * Triggered when the user clicks the Left Mouse Button (LMB)
	 * @param event Event fired.
	 */
	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		if(this.hovered && Aoba.getInstance().hudManager.isClickGuiOpen())  {
			this.onClick.run();
		}
	}
	
	@Override
	public void OnVisibilityChanged() {
		if(this.isVisible()) {
			Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		}else {
			Aoba.getInstance().eventManager.RemoveListener(LeftMouseDownListener.class, this);
		}
	}
}
