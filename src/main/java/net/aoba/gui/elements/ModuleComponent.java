package net.aoba.gui.elements;

import java.util.ArrayList;
import java.util.List;
import net.aoba.module.Module;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.tabs.ClickGuiTab;
import net.aoba.settings.Setting;
import net.aoba.settings.*;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ModuleComponent extends Component {

	private String text;
	private Module module;
	private ClickGuiTab parent;
	private boolean wasClicked = false;
	private boolean popped = false;
	private boolean hovered = false;
	
	private int expandedHeight = 30;
	
	private Color hoverColor = new Color(90, 90, 90);
	private Color color = new Color(128, 128, 128);

	private Color backgroundColor = color;

	private List<Component> settingsList = new ArrayList<Component>();

	public ModuleComponent(String text, ClickGuiTab parent, Module module) {
		super();
		this.text = text;
		this.parent = parent;
		this.module = module;
		for (Setting setting : this.module.getSettings()) {
			Component c;
			if (setting instanceof SliderSetting) {
				c = new SliderComponent(this.parent, (SliderSetting) setting);
			} else if (setting instanceof BooleanSetting) {
				c = new CheckboxComponent(this.parent, (BooleanSetting) setting);
			} else if (setting instanceof ListSetting) {
				c = new ListComponent(this.parent, (ListSetting) setting);
			} else {
				c = null;
			}
			settingsList.add(c);
		}
		
		RecalculateExpandedHeight();
	}

	public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		
		// If the Module options are popped, display all of the options.
		if (this.popped) {
			// Updates all of the options. 
			int i = offset + 30;
			for (Component children : this.settingsList) {
				children.update(i, mouseX, mouseY, mouseClicked);
				i += children.getHeight();
			}
		}
		
		// As long as no other Component is being grabbed.
		if (HudManager.currentGrabbed == null) {
			
			// Check if the current Module Component is currently hovered over.
			hovered = ((mouseX >= parentX && mouseX <= (parentX + parentWidth)) && (mouseY >= parentY + offset && mouseY <= (parentY + offset + 28)));
			if(hovered) {
				backgroundColor = hoverColor;
				boolean isOnOptionsButton = (mouseX >= (parentX + parentWidth - 34) && mouseX <= (parentX + parentWidth));
				if(isOnOptionsButton) {
					if (mouseClicked) {
						if (!this.wasClicked) {
							this.popped = !this.popped;
							
							if(this.popped) {
								this.setHeight(expandedHeight);
							}else {
								this.setHeight(30);
							}
							this.wasClicked = true;
							return;
						}
					}
				}else {
					if (mouseClicked) {
						if (!this.wasClicked) {
							module.toggle();
							this.wasClicked = true;
							return;
						}
					}
				}
				
				this.wasClicked = (mouseClicked && this.wasClicked);
			}else {
				backgroundColor = color;
				this.wasClicked = false;
			}
		}else {
			backgroundColor = hoverColor;
		}
	}

	@Override
	public void draw(int offset, DrawContext drawContext, float partialTicks, Color color) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		MatrixStack matrixStack = drawContext.getMatrices();
		//renderUtils.drawOutlinedBox(matrixStack, parentX + 2, parentY + offset, parentWidth - 4, this.getHeight() - 2, backgroundColor, 0.2f);

		if (this.popped) {
			int i = offset + 30;
			for (Component children : this.settingsList) {
				if (children.isVisible()) {
					children.draw(i, drawContext, partialTicks, color);
					i += children.getHeight();
				}
			}
		}

		renderUtils.drawString(drawContext, this.text, parentX + 8, parentY + 8 + offset,
				module.getState() ? 0x00FF00 : 0xFFFFFF);
		if (module.hasSettings()) {
			renderUtils.drawString(drawContext, this.popped ? "<<" : ">>", parentX + parentWidth - 30,
					parentY + 8 + offset, color.getColorAsInt());
		}
	}
	
	public void RecalculateExpandedHeight() {
		int height = 30;
		for (Component children : this.settingsList) {
			if (children.isVisible()) {
				height += children.getHeight();
			}
		}
		expandedHeight = height;
	}
}
