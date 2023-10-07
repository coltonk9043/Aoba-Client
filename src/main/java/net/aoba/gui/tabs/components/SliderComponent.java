package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.core.settings.types.FloatSetting;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.gui.Color;
import net.aoba.gui.IHudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class SliderComponent extends Component implements LeftMouseDownListener {

	private String text;
	private float currentSliderPosition = 0.4f;
	float r;
	float g;
	float b;

	FloatSetting slider;

	public SliderComponent(String text, IHudElement parent) {
		super(parent);
		this.text = text;
		this.slider = null;
		this.setHeight(24);
		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
	}

	public SliderComponent(IHudElement parent, FloatSetting slider) {
		super(parent);
		this.text = slider.displayName;
		this.slider = slider;
		this.currentSliderPosition = (float) ((slider.getValue() - slider.min_value)
				/ (slider.max_value - slider.min_value));
		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
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

	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		System.out.println("click");
		double mouseX = event.GetMouseX();
		if (hovered) {
			this.currentSliderPosition = (float) Math.min((((mouseX - ((actualX + 4))) - 1) / ((actualWidth - 12))),1f);
			this.currentSliderPosition = (float) Math.max(0f, this.currentSliderPosition);
			System.out.println("Inside slider at position: " + currentSliderPosition);
			this.slider.setValue((this.currentSliderPosition * (slider.max_value - slider.min_value)) + slider.min_value);
		}
	}

	@Override
	public void update() {

	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();
		renderUtils.drawBox(matrixStack, actualX, actualY, actualWidth, actualHeight, 0.5f, 0.5f, 0.5f, 0.3f);
		renderUtils.drawBox(matrixStack, actualX, actualY,
				(int) Math.floor(actualWidth)
						* (float) ((slider.getValue() - slider.min_value) / (slider.max_value - slider.min_value)),
				actualHeight, color, 1f);

		renderUtils.drawOutline(matrixStack, actualX, actualY, actualWidth, actualHeight);
		if (this.slider == null)
			return;
		// TODO: Slow but it works. Perhaps we can modify a STORED string using our new
		// Consumer delegates?
		renderUtils.drawString(drawContext, this.text + ": " + String.format("%.02f", this.slider.getValue()),
				actualX + 10, actualY + 6, 0xFFFFFF);
	}

}
