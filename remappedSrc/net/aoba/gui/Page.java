/*
* Aoba Hacked Client
* Copyright (C) 2019-2024 coltonk9043
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
	
	public void render(DrawContext drawContext, float partialTicks) {
		if(this.isVisible) {
			Iterator<AbstractGui> tabIterator =  tabs.iterator();
			while(tabIterator.hasNext()) {
				tabIterator.next().draw(drawContext, partialTicks);
			}
		}
	}
}
