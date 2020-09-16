package aoba.main.gui.elements;

import aoba.main.gui.Tab;
import net.minecraft.client.Minecraft;

public class StringComponent extends Component {

	private String text;
	private Tab parent;
	
	public StringComponent(int id, String text, Tab parent) {
		super(id);
		this.text = text;
		this.parent = parent;
	}

	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		
	}

	@Override
	public void draw() {
		int parentX = parent.getX();
		int parentY = parent.getY();
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(this.text, parentX + 5,
				parentY + 19 + (this.getId() * 15), 0xFFFFFF, true);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	

}