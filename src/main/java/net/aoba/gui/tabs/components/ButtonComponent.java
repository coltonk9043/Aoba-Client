package net.aoba.gui.tabs.components;

import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.tabs.ClickGuiTab;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ButtonComponent extends Component {

	private String text;
	private ClickGuiTab parent;
	private boolean wasClicked = false;
	private boolean hovered = false;
	private Runnable onClick;

	private Color hoverColor = new Color(90, 90, 90);
	private Color color = new Color(128, 128, 128);

	private Color backgroundColor = color;

	public ButtonComponent(ClickGuiTab parent, String text, Runnable onClick) {
		super();
		this.text = text;
		this.parent = parent;
		this.onClick = onClick;
	}

	public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
		float parentX = parent.getX();
		float parentY = parent.getY();
		float parentWidth = parent.getWidth();

		if (HudManager.currentGrabbed == null) {
			if (mouseClicked) {
				// Enable Module
				if((mouseX >= ((parentX + 2)) && mouseX <= (((parentX + 2)) + parentWidth - 34)) && 
				   (mouseY >= parentY + offset && mouseY <= (parentY + offset + 28))) {
					backgroundColor = hoverColor;
					hovered = true;
					if (!this.wasClicked) {
						try {
							onClick.run();
						} catch (Exception e) {
							e.printStackTrace();
						}
						this.wasClicked = true;
						return;
					}
				}else {
					this.backgroundColor = color;
					this.hovered = false;
					this.wasClicked = false;
				}
			} 
		}
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setOnClick(Runnable onClick) {
		this.onClick = onClick;
	}

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
}