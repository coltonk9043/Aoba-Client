package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.core.settings.Setting;
import net.aoba.core.settings.types.BooleanSetting;
import net.aoba.core.settings.types.FloatSetting;
import net.aoba.core.settings.types.IndexedStringListSetting;
import net.aoba.core.settings.types.StringListSetting;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.module.Module;
import net.aoba.gui.Color;
import net.aoba.gui.tabs.ClickGuiTab;
import net.minecraft.client.gui.DrawContext;

public class ModuleComponent extends Component implements LeftMouseDownListener {
	private String text;
	private Module module;
	private boolean popped = false;

	private int expandedHeight = 30;

	private Color hoverColor = new Color(90, 90, 90);
	private Color color = new Color(128, 128, 128);

	private Color backgroundColor = color;

	public ModuleComponent(String text, ClickGuiTab parent, Module module) {
		super(parent);
		this.text = text;
		this.module = module;
		
		this.setHeight(30);
		
		int i = 30;
		for (Setting setting : this.module.getSettings()) {
			Component c;
			if (setting instanceof FloatSetting) {
				c = new SliderComponent(this, (FloatSetting) setting);
			} else if (setting instanceof BooleanSetting) {
				c = new CheckboxComponent(this, (BooleanSetting) setting);
			} else if (setting instanceof StringListSetting) {
				c = new ListComponent(this, (IndexedStringListSetting) setting);
			} else {
				c = null;
			}
			
			c.setTop(i);
			c.setHeight(30);
			c.setVisible(false);
			children.add(c);
			i += 30;
		}

		RecalculateExpandedHeight();

		Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
	}

	@Override
	public void update() {

	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		super.draw(drawContext, partialTicks, color);
		renderUtils.drawString(drawContext, this.text, actualX + 8, actualY + 8,
				module.getState() ? 0x00FF00 : 0xFFFFFF);
		if (module.hasSettings()) {
			renderUtils.drawString(drawContext, this.popped ? "<<" : ">>", actualX + actualWidth - 30,
					actualY + 8, color.getColorAsInt());
		}
	}

	public void setPopped(boolean state) {
		this.popped = state;
		for(Component child : children) {
			child.setVisible(state);
		}
		RecalculateExpandedHeight();
	}
	
	public void RecalculateExpandedHeight() {
		int height = 30;
		for (Component child : this.children) {
			if (child.isVisible()) {
				height += child.getHeight();
			}
		}
		expandedHeight = height;
	}

	
	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		double mouseX = event.GetMouseX();
		double mouseY = event.GetMouseY();
		if (hovered) {
			if(mouseY >= actualY && mouseY <= actualY + 30){
				boolean isOnOptionsButton = (mouseX >= (actualX + actualWidth - 34) && mouseX <= (actualX + actualWidth));
				if (isOnOptionsButton) {
					setPopped(!this.popped);

					if (this.popped) {
						this.setHeight(expandedHeight);
					} else {
						this.setHeight(30);
					}
				} else {
					module.toggle();
					return;
				}
			}
		}
	}
}
