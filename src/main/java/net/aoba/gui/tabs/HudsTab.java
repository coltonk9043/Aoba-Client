package net.aoba.gui.tabs;

import java.util.ArrayList;
import java.util.List;

import net.aoba.Aoba;
import net.aoba.core.utils.types.Vector2;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.event.listeners.MouseScrollListener;
import net.aoba.gui.Color;
import net.aoba.gui.hud.AbstractHud;
import net.aoba.gui.tabs.components.ColorPickerComponent;
import net.aoba.gui.tabs.components.HudComponent;
import net.aoba.gui.tabs.components.StackPanelComponent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.aoba.module.Module;

public class HudsTab extends ClickGuiTab implements MouseScrollListener {

	int visibleScrollElements;
	int currentScroll;

	public HudsTab(List<AbstractHud> hudList) {
		super("Enable Huds", 50, 50, false);

		Aoba.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
		
		StackPanelComponent stackPanel = new StackPanelComponent(this);
		stackPanel.setTop(30);
		
		for(AbstractHud hud : hudList) {
			HudComponent hudComponent = new HudComponent(hud.getID(), stackPanel, hud);

			stackPanel.addChild(hudComponent);
		}
		
		stackPanel.addChild(new ColorPickerComponent("Hud Color", stackPanel, Aoba.getInstance().hudManager.color));
		
		this.children.add(stackPanel);
		this.setWidth(300);
	}
	
	@Override
	public void OnMouseScroll(MouseScrollEvent event) {
		 ArrayList<Module> modules = Aoba.getInstance().moduleManager.modules;
		 
		 if(event.GetVertical() > 0) 
			 this.currentScroll = Math.min(currentScroll + 1, modules.size() - visibleScrollElements - 1); 
		 else if(event.GetVertical() < 0) 
			 this.currentScroll = Math.max(currentScroll - 1, 0);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		super.draw(drawContext, partialTicks, color);
	}
}
