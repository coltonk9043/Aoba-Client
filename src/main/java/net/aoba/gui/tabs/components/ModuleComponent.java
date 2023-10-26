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
import net.aoba.gui.IHudElement;
import net.aoba.gui.tabs.ClickGuiTab;
import net.aoba.gui.tabs.ModuleSettingsTab;
import net.minecraft.client.gui.DrawContext;

public class ModuleComponent extends Component implements LeftMouseDownListener {
	private String text;
	private Module module;
	private Color hoverColor = new Color(90, 90, 90);
	private Color color = new Color(128, 128, 128);

	private Color backgroundColor = color;

	private ModuleSettingsTab lastSettingsTab = null;
	
	public ModuleComponent(String text, IHudElement parent, Module module) {
		super(parent);
		this.text = text;
		this.module = module;
		
		this.setLeft(2);
		this.setRight(2);
		this.setHeight(30);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		super.draw(drawContext, partialTicks, color);
		renderUtils.drawString(drawContext, this.text, actualX + 8, actualY + 8,
				module.getState() ? 0x00FF00 : 0xFFFFFF);
		if (module.hasSettings()) {
			renderUtils.drawString(drawContext, ">>", actualX + actualWidth - 30,
					actualY + 8, color.getColorAsInt());
		}
	}
	
	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		double mouseX = event.GetMouseX();
		double mouseY = event.GetMouseY();
		
		if (hovered) {
				boolean isOnOptionsButton = (mouseX >= (actualX + actualWidth - 34) && mouseX <= (actualX + actualWidth));
				if (isOnOptionsButton) {
					if(lastSettingsTab == null) {
						lastSettingsTab = new ModuleSettingsTab(this.module.getName(), this.actualX + this.width + 1, this.y, this.module);
						lastSettingsTab.setVisible(true);
						Aoba.getInstance().hudManager.AddHud(lastSettingsTab, "Modules");
					}else {
						Aoba.getInstance().hudManager.RemoveHud(lastSettingsTab, "Modules");
						lastSettingsTab = null;
					}
				} else {
					module.toggle();
					return;
				}
		}
	}
	
	@Override
	public void OnVisibilityChanged() {
		if(this.isVisible()) {
			Aoba.getInstance().eventManager.AddListener(LeftMouseDownListener.class, this);
		}else {
			Aoba.getInstance().eventManager.RemoveListener(LeftMouseDownListener.class, this);
		}
	}
}
