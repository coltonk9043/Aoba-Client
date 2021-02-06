package aoba.main.gui.elements;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.system.MathUtil;
import aoba.main.gui.HudManager;
import aoba.main.gui.Tab;
import aoba.main.module.Module;
import net.minecraft.client.Minecraft;

public class SliderComponent extends Component {

	private String text;
	private Module module;
	private Tab parent;
	private boolean hasSettings;
	private float currentSliderPosition = 0.4f;
	float r;
	float g;
	float b;
	
	public SliderComponent(int id, String text, Tab parent) {
		super(id);
		this.text = text;
		this.parent = parent;
		this.module = null;
	}
	
	public SliderComponent(int id, String text, Tab parent, Module module) {
		super(id);
		this.text = text;
		this.parent = parent;
		this.module = module;
	}
	
	@Override
	public void update(double mouseX, double mouseY,  boolean mouseClicked) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		if (HudManager.currentGrabbed == null) {
			if (mouseX >= ((parentX + 1) * 2) && mouseX <= (((parentX + 1)) + parentWidth - 2) * 2) {
				if (mouseY >= (((parentY + 15 + (this.getId() * 15))) * 2)
						&& mouseY <= ((parentY + 15 + (this.getId() * 15)) + 14) * 2) {
					if (mouseClicked) {
						this.currentSliderPosition = (float) Math.min((((mouseX - ((parentX)) * 2) - 1 )/((parentWidth - 2) * 2)) , 1f);
					}
				}
			}
		}
	}
	
	@Override
	public void draw() {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		
		renderUtils.drawBox(parentX + 1, parentY + 15 + (this.getId() * 15), parentWidth - 2, 14, 0.3f, 0.3f, 0.3f, 0.1f);
		renderUtils.drawBox(parentX + 1, parentY + 15 + (this.getId() * 15), (int)(parentWidth * this.currentSliderPosition) - 2, 14, this.r, this.g, this.b, 1f);
		renderUtils.drawOutline(parentX + 1, parentY + 15 + (this.getId() * 15), parentWidth - 2, 14);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(this.text, parentX + 5, parentY + 19 + (this.getId() * 15), 0xFFFFFF, true);
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
