package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.IHudElement;
import net.aoba.gui.tabs.ClickGuiTab;
import net.aoba.settings.types.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class CheckboxComponent extends Component implements LeftMouseDownListener {
	private String text;
	private BooleanSetting checkbox;
	private Runnable onClick;
	
	public CheckboxComponent(IHudElement parent, BooleanSetting checkbox) {
		super(parent);
		this.text = checkbox.displayName;
		this.checkbox = checkbox;

		this.setLeft(2);
		this.setRight(2);
		this.setHeight(30);
		
		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
	}

	/**
	 * Draws the checkbox to the screen.
	 * @param offset The offset (Y location relative to parent) of the Component.
	 * @param drawContext The current draw context of the game.
	 * @param partialTicks The partial ticks used for interpolation.
	 * @param color The current Color of the UI.
	 */
	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		super.draw(drawContext, partialTicks, color);
		
		MatrixStack matrixStack = drawContext.getMatrices();
		renderUtils.drawString(drawContext, this.text, actualX + 10, actualY + 8, 0xFFFFFF);
		if (this.checkbox.getValue()) {
			renderUtils.drawOutlinedBox(matrixStack, actualX + actualWidth - 24, actualY + 5, 20, 20,
					new Color(0, 154, 0), 0.8f);
		} else {
			renderUtils.drawOutlinedBox(matrixStack, actualX + actualWidth - 24, actualY + 5, 20, 20,
					new Color(154, 0, 0), 0.8f);
		}
	}

	/**
	 * Handles updating the Checkbox component.
	 * @param offset The offset (Y position relative to parent) of the Checkbox.
	 */
	@Override
	public void update() {
		super.update();
	}

	/**
	 * Triggered when the user clicks the Left Mouse Button (LMB)
	 * 
	 * @param event Event fired.
	 */
	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		if (hovered && Aoba.getInstance().hudManager.isClickGuiOpen()) {
			checkbox.toggle();
			if(onClick != null) {
				onClick.run();
			}
		}
	}
}
