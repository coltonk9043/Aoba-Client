package net.aoba.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.aoba.gui.hud.AbstractHud;
import net.minecraft.client.gui.DrawContext;

public class Page {
	protected String title;
	protected List<AbstractHud> tabs = new ArrayList<AbstractHud>();

	private boolean isVisible;
	
	public Page(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void AddHud(AbstractHud hud) {
		tabs.add(hud);
	}
	
	public void setVisible(boolean state) {
		this.isVisible = state;
		for(AbstractHud hud : tabs){
			hud.setVisible(state);
		}
	}
	
	public void update() {
		if(this.isVisible) {
			Iterator<AbstractHud> tabIterator =  tabs.iterator();
			while(tabIterator.hasNext()) {
				tabIterator.next().update();
			}
		}
	}
	
	public void render(DrawContext drawContext, float partialTicks, Color color) {
		if(this.isVisible) {
			Iterator<AbstractHud> tabIterator =  tabs.iterator();
			while(tabIterator.hasNext()) {
				tabIterator.next().draw(drawContext, partialTicks, color);
			}
		}
	}
}
