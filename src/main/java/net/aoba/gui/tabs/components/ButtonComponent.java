package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.tabs.ClickGuiTab;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ButtonComponent extends Component implements LeftMouseDownListener {

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

		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
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
		renderUtils.drawOutlinedBox(matrixStack, actualX + 2, actualY, actualWidth - 4, actualHeight - 2,
				backgroundColor, 0.2f);

		renderUtils.drawString(drawContext, this.text, actualY + 8, actualY + 8, 0xFFFFFF);
	}

	/**
	 * Triggered when the user clicks the Left Mouse Button (LMB)
	 * @param event Event fired.
	 */
	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		double mouseX = event.GetMouseX();
		double mouseY = event.GetMouseY();

		if (HudManager.currentGrabbed == null) {
			// If our delegate exists and we are inside the bounds of the button, run it.
			if(this.onClick != null) {
				if ((mouseX >= actualX && mouseX <= actualX + actualWidth - 34)
						&& (mouseY >= actualY && mouseY <= actualY + actualHeight)) {
					this.onClick.run();
				}
			}
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
