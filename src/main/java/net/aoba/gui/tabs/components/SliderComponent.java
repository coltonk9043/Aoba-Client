package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.core.settings.types.FloatSetting;
import net.aoba.core.utils.types.Vector2;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.events.LeftMouseUpEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.event.listeners.LeftMouseUpListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.IHudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class SliderComponent extends Component implements LeftMouseDownListener, LeftMouseUpListener, MouseMoveListener {

	private String text;
	private float currentSliderPosition = 0.4f;
	float r;
	float g;
	float b;
	private boolean isSliding = false;
	
	
	FloatSetting slider;

	public SliderComponent(String text, IHudElement parent) {
		super(parent);
		this.text = text;
		this.slider = null;
		
		
		this.setHeight(24);
		this.setLeft(4);
		this.setRight(4);

		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		Aoba.getInstance().eventManager.AddListener(LeftMouseUpListener.class, this);
	}

	public SliderComponent(IHudElement parent, FloatSetting slider) {
		super(parent);
		this.text = slider.displayName;
		this.slider = slider;
		this.currentSliderPosition = (float) ((slider.getValue() - slider.min_value)
				/ (slider.max_value - slider.min_value));
		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		Aoba.getInstance().eventManager.AddListener(LeftMouseUpListener.class, this);
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
		if (hovered) {
			isSliding = true;
		}
	}
	
	@Override
	public void OnLeftMouseUp(LeftMouseUpEvent event) {
		isSliding = false;
	}
	
	@Override
	public void OnMouseMove(MouseMoveEvent event) {
		super.OnMouseMove(event);
		
		double mouseX = event.GetHorizontal();
		if (Aoba.getInstance().hudManager.isClickGuiOpen() && this.isSliding) {
			this.currentSliderPosition = (float) Math.min((((mouseX - (actualX + 4)) - 1) / (actualWidth - 8)),1f);
			this.currentSliderPosition = (float) Math.max(0f, this.currentSliderPosition);
			this.slider.setValue((this.currentSliderPosition * (slider.max_value - slider.min_value)) + slider.min_value);
		}
	}


	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();
		renderUtils.drawBox(matrixStack, actualX + 4, actualY + 4, actualWidth - 8, actualHeight - 8, 0.5f, 0.5f, 0.5f, 0.3f);
		renderUtils.drawBox(matrixStack, actualX + 4, actualY + 4, (actualWidth - 8) * (float) ((slider.getValue() - slider.min_value) / (slider.max_value - slider.min_value)), actualHeight - 8, color, 1f);
		renderUtils.drawOutline(matrixStack, actualX + 4, actualY + 4, actualWidth - 8, actualHeight - 8);
		if (this.slider == null)
			return;
		// TODO: Slow but it works. Perhaps we can modify a STORED string using our new
		// Consumer delegates?
		renderUtils.drawString(drawContext, this.text + ": " + String.format("%.02f", this.slider.getValue()),
				actualX + 10, actualY + 6, 0xFFFFFF);
	}
}
