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
	private boolean wasClicked = false;

	BooleanSetting checkbox;
	
	public CheckboxComponent(ClickGuiTab parent, BooleanSetting checkbox) {
		super();
		this.text = checkbox.displayName;
		this.parent = parent;
		this.checkbox = checkbox;
		
		Aoba.getInstance().eventManager.AddListener(MouseLeftClickListener.class, this);
	}

	@Override
	public void draw(int offset, DrawContext drawContext, float partialTicks, Color color) {
		float parentX = parent.getX();
		float parentY = parent.getY();
		float parentWidth = parent.getWidth();
		MatrixStack matrixStack = drawContext.getMatrices();
		renderUtils.drawString(drawContext, this.text, parentX + 10,
				parentY + offset + 8, 0xFFFFFF);
		if(this.checkbox.getValue()) {
			renderUtils.drawOutlinedBox(matrixStack, parentX + parentWidth - 24, parentY + 1 + offset, 20, 20, new Color(0,154,0), 0.8f);
		}else {
			renderUtils.drawOutlinedBox(matrixStack, parentX + parentWidth - 24, parentY + 1 + offset, 20, 20, new Color(154,0,0), 0.8f);
		}
	}

	@Override
	public void update(int offset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnMouseLeftClick(MouseLeftClickEvent event) {
		float parentX = parent.getX();
		float parentY = parent.getY();
		float parentWidth = parent.getWidth();
		
		int mouseX = event.GetMouseX();
		int mouseY = event.GetMouseY();
		
		if (HudManager.currentGrabbed == null) {
			if (!this.wasClicked) {
				if (mouseX >= ((parentX + parent.getWidth() - 28))
						&& mouseX <= ((parentX + parentWidth - 8))) {
					if (mouseY >= (((parentY + offset + 2)))
							&& mouseY <= (parentY + offset + 22)) {
						if (!this.wasClicked) {
							checkbox.toggle();
							this.wasClicked = true;
						}
					}
				}
			}
		}
	}
}
