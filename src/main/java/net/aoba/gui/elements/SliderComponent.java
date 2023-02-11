package net.aoba.gui.elements;

import net.aoba.gui.ClickGuiTab;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.util.math.MatrixStack;

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
		if (HudManager.currentGrabbed == null) {
			if (mouseClicked) {
				if (mouseX >= ((parentX + 2)) && mouseX <= (((parentX)) + parentWidth - 2)) {
					if (mouseY >= (((parentY + offset)))
							&& mouseY <= ((parentY + offset) + 24)) {
						this.currentSliderPosition = (float) Math.min((((mouseX - ((parentX + 4))) - 1) / ((parentWidth - 12))), 1f);
						this.currentSliderPosition = (float) Math.max(0f,this.currentSliderPosition);
						this.slider.setValue((this.currentSliderPosition * this.slider.getRange()) + this.slider.getMinimum());
					}
				}
			}
		}
	}

	@Override
	public void draw(int offset, MatrixStack matrixStack, float partialTicks, Color color) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();

		renderUtils.drawBox(matrixStack, parentX + 3, parentY + offset, parentWidth - 6, 24, 0.5f, 0.5f, 0.5f,
				0.3f);
		renderUtils.drawBox(matrixStack, parentX + 3, parentY + offset,
				(int) Math.floor((parentWidth - 6) * this.currentSliderPosition), 24, color, 1f);
		renderUtils.drawOutline(matrixStack, parentX + 3, parentY + offset, parentWidth - 6, 24);
		if(this.slider == null) return;
		renderUtils.drawStringWithScale(matrixStack, this.text + ": " + this.slider.getValueFloat(), parentX + 10,
				parentY + 6 + offset, 0xFFFFFF);
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
