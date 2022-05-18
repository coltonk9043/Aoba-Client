package net.aoba.gui.elements;

import net.aoba.gui.ClickGuiTab;
import net.aoba.gui.Color;
import net.minecraft.client.util.math.MatrixStack;

public class StringComponent extends Component {

	private String text;
	private boolean bold;
	
	public StringComponent(String text, ClickGuiTab parent) {
		super();
		this.text = text;
		this.bold = false;
		this.parent = parent;
	}
	
	public StringComponent(String text, ClickGuiTab parent, boolean bold) {
		super();
		this.text = text;
		this.bold = bold;
		this.parent = parent;
	}

	public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
		
	}

	@Override
	public void draw(int offset, MatrixStack matrixStack, float partialTicks, Color color) {
		int parentX = this.parent.getX();
		int parentY = this.parent.getY();
		renderUtils.drawStringWithScale(matrixStack, this.text, parentX + 10,
				parentY + 8 + offset, 0xFFFFFF);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	

}