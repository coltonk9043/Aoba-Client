package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.event.events.MouseLeftClickEvent;
import net.aoba.event.listeners.MouseLeftClickListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.tabs.ClickGuiTab;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ButtonComponent extends Component implements MouseLeftClickListener {

	private String text;
	private Runnable onClick;

	private Color hoverColor = new Color(90, 90, 90);
	private Color color = new Color(128, 128, 128);
	private Color backgroundColor = color;
	
	/**
	 * Constructor for button component.
	 * @param parent Parent Tab that this Component resides in.
	 * @param text Text contained in this button element.
	 * @param onClick OnClick delegate that will run when the button is pressed.
	 */
	public ButtonComponent(ClickGuiTab parent, String text, Runnable onClick) {
		super(parent);
		this.text = text;
		this.onClick = onClick;

		Aoba.getInstance().eventManager.AddListener(MouseLeftClickListener.class, this);
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
	public void draw(int offset, DrawContext drawContext, float partialTicks, Color color) {
		float parentX = parent.getX();
		float parentY = parent.getY();
		float parentWidth = parent.getWidth();
		MatrixStack matrixStack = drawContext.getMatrices();
		renderUtils.drawOutlinedBox(matrixStack, parentX + 2, parentY + offset, parentWidth - 4, this.getHeight() - 2,
				backgroundColor, 0.2f);

		renderUtils.drawString(drawContext, this.text, parentX + 8, parentY + 8 + offset, 0xFFFFFF);
	}

	/**
	 * Triggered when the user clicks the Left Mouse Button (LMB)
	 * @param event Event fired.
	 */
	@Override
	public void OnMouseLeftClick(MouseLeftClickEvent event) {
		float parentX = parent.getX();
		float parentY = parent.getY();
		float parentWidth = parent.getWidth();

		double mouseX = event.GetMouseX();
		double mouseY = event.GetMouseY();

		if (HudManager.currentGrabbed == null) {
			// If our delegate exists and we are inside the bounds of the button, run it.
			if(this.onClick != null) {
				if ((mouseX >= ((parentX + 2)) && mouseX <= (((parentX + 2)) + parentWidth - 34))
						&& (mouseY >= parentY + offset && mouseY <= (parentY + offset + 28))) {
					this.onClick.run();
				}
			}
		}
	}
}
