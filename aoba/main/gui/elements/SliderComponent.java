package aoba.main.gui.elements;

import aoba.main.gui.HudManager;
import aoba.main.gui.ClickGuiTab;
import aoba.main.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import aoba.main.gui.Color;

public class SliderComponent extends Component {

	private String text;
	private ClickGuiTab parent;
	private float currentSliderPosition = 0.4f;
	float r;
	float g;
	float b;
	
	SliderSetting slider;

	public SliderComponent(String text, ClickGuiTab parent) {
		super();
		this.text = text;
		this.parent = parent;
		this.slider = null;
	}

	public SliderComponent(ClickGuiTab parent, SliderSetting slider) {
		super();
		this.text = slider.getName();
		this.parent = parent;
		this.slider = slider;
		this.currentSliderPosition = (float) ((slider.getValue() - slider.getMinimum()) / slider.getRange());
	}

	@Override
	public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		Minecraft mc = Minecraft.getInstance();
		if (HudManager.currentGrabbed == null) {
			if (mouseClicked) {
				if (mouseX >= ((parentX + 2) * mc.gameSettings.guiScale) && mouseX <= (((parentX)) + parentWidth - 2) * mc.gameSettings.guiScale) {
					if (mouseY >= (((parentY + offset)) * mc.gameSettings.guiScale)
							&& mouseY <= ((parentY + offset) + 12) * mc.gameSettings.guiScale) {
						this.currentSliderPosition = (float) Math.min((((mouseX - ((parentX + 2)) * mc.gameSettings.guiScale) - 1) / ((parentWidth - 6) * mc.gameSettings.guiScale)), 1f);
						this.currentSliderPosition = (float) Math.max(0f,this.currentSliderPosition);
						this.slider.setValue((this.currentSliderPosition * this.slider.getRange()) + this.slider.getMinimum());
					}
				}
			}
		}
	}

	@Override
	public void draw(int offset, int scaledWidth, int scaledHeight, Color color) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();

		renderUtils.drawBox(parentX + 3, parentY + offset, parentWidth - 6, 12, 0.5f, 0.5f, 0.5f,
				0.3f);
		renderUtils.drawBox(parentX + 3, parentY + offset,
				(int) Math.floor((parentWidth * this.currentSliderPosition) - 6), 12, color, 1f);
		renderUtils.drawOutline(parentX + 3, parentY + offset, parentWidth - 6, 12);
		if(this.slider == null) return;
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(this.text + ": " + this.slider.getValueFloat(), parentX + 5,
				parentY + 3 + offset, 0xFFFFFF, true);
	}

	public float getSliderPosition() {
		return this.currentSliderPosition;
	}

	public void setSliderPosition(float pos) {
		this.currentSliderPosition = pos;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

}
