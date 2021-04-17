package aoba.main.gui.elements;

import aoba.main.gui.ClickGuiTab;
import net.minecraft.client.Minecraft;
import aoba.main.gui.Color;

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
	public void draw(int offset, int scaledWidth, int scaledHeight, Color color) {
		int parentX = this.parent.getX();
		int parentY = this.parent.getY();
		Minecraft.getInstance().fontRenderer.drawStringWithShadow((this.bold ? "§l" : "") + this.text, parentX + 5,
				parentY + 4 + offset, 0xFFFFFF, true);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	

}