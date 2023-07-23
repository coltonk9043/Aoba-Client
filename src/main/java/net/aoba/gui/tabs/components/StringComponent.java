package net.aoba.gui.tabs.components;

import net.aoba.gui.Color;
import net.aoba.gui.tabs.ClickGuiTab;
import net.minecraft.client.gui.DrawContext;

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
	public void draw(int offset, DrawContext drawContext, float partialTicks, Color color) {
		int parentX = this.parent.getX();
		int parentY = this.parent.getY();
		renderUtils.drawString(drawContext, this.text, parentX + 10,
				parentY + 8 + offset, 0xFFFFFF);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	

}