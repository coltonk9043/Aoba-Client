package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.events.LeftMouseUpEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.event.listeners.LeftMouseUpListener;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.Color;
import net.aoba.gui.IHudElement;
import net.aoba.settings.types.ColorSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ColorPickerComponent extends Component implements LeftMouseDownListener, LeftMouseUpListener, MouseMoveListener {

	private String text;
	private boolean isSliding = false;
	private boolean collapsed = false;
	private float hue = 0.0f;
	private float saturation = 0.0f;
	private float luminance = 0.0f;
	private float alpha = 0.0f;
	
	private ColorSetting color;

	public ColorPickerComponent(String text, IHudElement parent) {
		super(parent);
		this.text = text;
		
		this.setHeight(145);
		this.setLeft(4);
		this.setRight(4);

		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		Aoba.getInstance().eventManager.AddListener(LeftMouseUpListener.class, this);
	}
	
	public ColorPickerComponent(IHudElement parent, ColorSetting color) {
		super(parent);
		
		this.text = color.displayName;
		this.color = color;
		this.color.setOnUpdate((Color newColor) -> ensureGuiUpdated(newColor));
	
		this.hue = color.getValue().hue;
		this.saturation = color.getValue().saturation;
		this.luminance = color.getValue().luminance;
		
		this.setHeight(145);
		this.setLeft(4);
		this.setRight(4);
		
		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		Aoba.getInstance().eventManager.AddListener(LeftMouseUpListener.class, this);
	}
	
	public void ensureGuiUpdated(Color newColor) {
		this.hue = newColor.hue;
		this.saturation = newColor.saturation;
		this.luminance = newColor.luminance;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		if (hovered && Aoba.getInstance().hudManager.isClickGuiOpen()) {
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
		double mouseY = event.GetVertical();
		if (Aoba.getInstance().hudManager.isClickGuiOpen() && this.isSliding) {

			float vertical = (float) Math.min(Math.max(1.0f - (((mouseY - (actualY + 29)) - 1) / (actualHeight - 33)), 0.0f), 1.0f);
			
			// If inside of saturation/lightness box.
			if(mouseX >= actualX + 4 && mouseX <= actualX + actualWidth - 74) {
				float horizontal = (float) Math.min(Math.max(((mouseX - (actualX + 4)) - 1) / (actualWidth - 74), 0.0f), 1.0f);
				
				this.luminance = vertical;
				this.saturation = horizontal;
			}else if(mouseX >= actualX + actualWidth - 70 && mouseX <= actualX + actualWidth - 32) {
				this.hue = (1.0f - vertical) * 360.0f;
			}else if(mouseX >= actualX + actualWidth - 36 && mouseX <= actualX + actualWidth - 4) {
				this.alpha = (vertical) * 255.0f;
			}
			
			this.color.getValue().setHSV(hue, saturation, luminance);
			this.color.getValue().setAlpha((int) alpha);
		}
	}


	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		
		MatrixStack matrixStack = drawContext.getMatrices();
		
		renderUtils.drawString(drawContext, this.text, actualX + 6, actualY + 6, 0xFFFFFF);
		
		//String valueText = String.format("%.02f", this.slider.getValue());
		//int textSize = MinecraftClient.getInstance().textRenderer.getWidth(valueText) * MinecraftClient.getInstance().options.getGuiScale().getValue();
		//renderUtils.drawString(drawContext, valueText, actualX + actualWidth - 6 - textSize, actualY + 6, 0xFFFFFF);
		
		Color newColor = new Color(255, 0, 0);
		newColor.setHSV(this.hue, 1.0f, 1.0f);
		renderUtils.drawHorizontalGradient(matrixStack, actualX + 4, actualY + 29, actualX + actualWidth - 74, actualY + actualHeight - 4, new Color(255, 255, 255), newColor);
		renderUtils.drawVerticalGradient(matrixStack, actualX + 4, actualY + actualHeight - 4, actualX + actualWidth - 74, actualY + 29, new Color(0, 0, 0, 0), new Color(0, 0, 0));
		
		// Draw Hue Rectangle
		int increment = (int) ((this.actualHeight - 8) / 7);
		renderUtils.drawVerticalGradient(matrixStack, actualX + actualWidth - 70, actualY + 29, actualX + actualWidth - 40, actualY + 29 + increment, new Color(255, 255, 0), new Color(255, 0, 0));
		renderUtils.drawVerticalGradient(matrixStack, actualX + actualWidth - 70, actualY + 29 + increment, actualX + actualWidth - 40, actualY + 29 + (2 * increment), new Color(0, 255, 0), new Color(255, 255, 0));
		renderUtils.drawVerticalGradient(matrixStack, actualX + actualWidth - 70, actualY + 29 + (2 * increment), actualX + actualWidth - 40, actualY + 29 + (3 * increment), new Color(0, 255, 255), new Color(0, 255, 0));
		renderUtils.drawVerticalGradient(matrixStack, actualX + actualWidth - 70, actualY + 29 + (3 * increment), actualX + actualWidth - 40, actualY + 29 + (4 * increment), new Color(0, 0, 255), new Color(0, 255, 255));
		renderUtils.drawVerticalGradient(matrixStack, actualX + actualWidth - 70, actualY + 29 + (4 * increment), actualX + actualWidth - 40, actualY + 29 + (5 * increment), new Color(255, 0, 255), new Color(0, 0, 255));
		renderUtils.drawVerticalGradient(matrixStack, actualX + actualWidth - 70, actualY + 29 + (5 * increment), actualX + actualWidth - 40, actualY + actualHeight - 4, new Color(255, 0, 0), new Color(255, 0, 255));
	
		// Draw Alpha Rectangle
		renderUtils.drawVerticalGradient(matrixStack, actualX + actualWidth - 36 , actualY + 29, actualX + actualWidth - 4, actualY + actualHeight - 4, new Color(0, 0, 0), new Color(255, 255, 255));
		
		// Draw Outlines
		renderUtils.drawOutline(matrixStack, actualX + 4, actualY + 29, actualWidth - 78, actualHeight - 33);
		renderUtils.drawOutline(matrixStack, actualX + actualWidth - 70, actualY + 29, 30f, actualHeight - 33);
		renderUtils.drawOutline(matrixStack, actualX + actualWidth - 35, actualY + 29, 30f, actualHeight - 33);
		
		// Draw Indicators
		renderUtils.drawCircle(matrixStack, actualX + 4 + (saturation * (actualWidth - 74)), actualY + 29 + ((1.0f - luminance) * (actualHeight - 33)), 3, new Color(255, 255, 255, 255));
		renderUtils.drawOutlinedBox(matrixStack, actualX + actualWidth - 70, actualY + 29 + ((hue / 360.0f) * (actualHeight - 33)), 30, 3, new Color(255, 255, 255, 255));
		renderUtils.drawOutlinedBox(matrixStack, actualX + actualWidth - 36, actualY + 29 + (((255.0f - alpha) / 255.0f) * (actualHeight - 33)), 30, 3, new Color(255, 255, 255, 255));
	}
}