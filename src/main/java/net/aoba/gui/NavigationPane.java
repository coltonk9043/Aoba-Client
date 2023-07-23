package net.aoba.gui;

import java.util.ArrayList;
import java.util.List;

import net.aoba.gui.hud.AbstractHud;
import net.minecraft.client.gui.DrawContext;

public class NavigationPane {
	protected String title;
	protected List<AbstractHud> tabs = new ArrayList<AbstractHud>();
	
	public NavigationPane(String title) {
		this.title = title;
	}
	
	public void AddHud(AbstractHud hud) {
		tabs.add(hud);
	}
	
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		for(AbstractHud tab : tabs) {
			tab.update(mouseX, mouseY, mouseClicked);
		}
	}
	
	public void render(DrawContext drawContext, float partialTicks, Color color) {
		for(AbstractHud tab : tabs) {
			tab.draw(drawContext, partialTicks, color);
		}
	}
}
