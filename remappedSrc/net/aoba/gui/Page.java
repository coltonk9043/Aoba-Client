package net.aoba.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.DrawContext;

public class Page {
	protected String title;
	protected List<AbstractGui> tabs = new ArrayList<AbstractGui>();

	private boolean isVisible;
	
	public Page(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void AddHud(AbstractGui hud) {
		tabs.add(hud);
	}
	
	public void setVisible(boolean state) {
		this.isVisible = state;
		for(AbstractGui hud : tabs){
			hud.setVisible(state);
		}
	}
	
	public void update() {
		if(this.isVisible) {
			Iterator<AbstractGui> tabIterator =  tabs.iterator();
			while(tabIterator.hasNext()) {
				tabIterator.next().update();
			}
		}
	}
	
	public void render(DrawContext drawContext, float partialTicks, Color color) {
		if(this.isVisible) {
			Iterator<AbstractGui> tabIterator =  tabs.iterator();
			while(tabIterator.hasNext()) {
				tabIterator.next().draw(drawContext, partialTicks, color);
			}
		}
	}
}
