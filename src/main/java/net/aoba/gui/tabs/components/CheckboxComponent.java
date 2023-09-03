package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.core.settings.types.BooleanSetting;
import net.aoba.event.events.MouseLeftClickEvent;
import net.aoba.event.listeners.MouseLeftClickListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.tabs.ClickGuiTab;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class CheckboxComponent extends Component implements MouseLeftClickListener {
	private String text;
	private BooleanSetting checkbox;
	private Runnable onClick;
	
	public CheckboxComponent(ClickGuiTab parent, BooleanSetting checkbox) {
		super();
		this.text = checkbox.displayName;
		this.parent = parent;
		this.checkbox = checkbox;

		Aoba.getInstance().eventManager.AddListener(MouseLeftClickListener.class, this);
	}

	/**
	 * Draws the checkbox to the screen.
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
		renderUtils.drawString(drawContext, this.text, parentX + 10, parentY + offset + 8, 0xFFFFFF);
		if (this.checkbox.getValue()) {
			renderUtils.drawOutlinedBox(matrixStack, parentX + parentWidth - 24, parentY + 1 + offset, 20, 20,
					new Color(0, 154, 0), 0.8f);
		} else {
			renderUtils.drawOutlinedBox(matrixStack, parentX + parentWidth - 24, parentY + 1 + offset, 20, 20,
					new Color(154, 0, 0), 0.8f);
		}
	}

	/**
	 * Handles updating the Checkbox component.
	 * @param offset The offset (Y position relative to parent) of the Checkbox.
	 */
	@Override
	public void update(int offset) {
	}

	/**
	 * Triggered when the user clicks the Left Mouse Button (LMB)
	 * 
	 * @param event Event fired.
	 */
	@Override
	public void OnMouseLeftClick(MouseLeftClickEvent event) {
		if (HudManager.currentGrabbed == null && hovered) {
			checkbox.toggle();
			if(onClick != null) {
				onClick.run();
			}
		}
	}
}
