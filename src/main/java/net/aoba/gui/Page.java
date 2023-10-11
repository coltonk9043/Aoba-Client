package net.aoba.gui;

import java.util.ArrayList;
import java.util.List;

import net.aoba.gui.hud.AbstractHud;
import net.minecraft.client.gui.DrawContext;

public class Page {
	protected String title;
	protected List<AbstractHud> tabs = new ArrayList<AbstractHud>();
	
	private List<AbstractHud> tabsToAdd = new ArrayList<AbstractHud>();
	
	public Page(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void AddHud(AbstractHud hud) {
		tabsToAdd.add(hud);
	}
	
	public void update() {
		for(AbstractHud tabToAdd : tabsToAdd) {
			tabs.add(tabToAdd);
		}
		tabsToAdd.clear();
		
		for(AbstractHud tab : tabs) {
			tab.update();
		}
	}
	
	public void render(DrawContext drawContext, float partialTicks, Color color) {
		for(AbstractHud tab : tabs) {
			tab.draw(drawContext, partialTicks, color);
		}
	}
}
