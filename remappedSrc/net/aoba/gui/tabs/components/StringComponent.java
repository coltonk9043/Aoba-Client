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

package net.aoba.gui.tabs.components;

import java.util.ArrayList;

import net.aoba.Aoba;
import net.aoba.event.events.FontChangedEvent;
import net.aoba.event.listeners.FontChangedListener;
import net.aoba.event.listeners.LeftMouseDownListener;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class StringComponent extends Component implements FontChangedListener {
	private String originalText;
	private ArrayList<String> text;
	private boolean bold;
	private Color color;
	
	public StringComponent(String text, IGuiElement parent) {
		super(parent);
		setText(text);
		this.color = Colors.White;
		this.bold = false;
		
		Aoba.getInstance().eventManager.AddListener(FontChangedListener.class, this);
	}

	public StringComponent(String text, IGuiElement parent, boolean bold) {
		super(parent);
		setText(text);
		this.color = Colors.White;
		this.bold = bold;
		
		Aoba.getInstance().eventManager.AddListener(FontChangedListener.class, this);
	}
	
	public StringComponent(String text, IGuiElement parent, Color color, boolean bold) {
		super(parent);
		setText(text);
		this.color = color;
		this.bold = bold;
		
		Aoba.getInstance().eventManager.AddListener(FontChangedListener.class, this);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		int i = 0;
		for (String str : text) {
			if(bold)
				str = Formatting.BOLD + str;
			RenderUtils.drawString(drawContext, str, actualX + 8, actualY + 8 + i, this.color.getColorAsInt());
			i += 30;
		}
	}

	/**
	 * Sets the text of the String Component.
	 * 
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this.originalText = text;
		this.text = new ArrayList<String>();
		
		float textWidth = Aoba.getInstance().fontManager.GetRenderer().getWidth(text) * 2.0f;
		int strings = (int) Math.ceil(textWidth / this.actualWidth);
		if (strings == 0) {
			this.text.add(text);
			this.setHeight(30);
		} else {
			int lengthOfEachSegment = text.length() / strings;

			for (int i = 0; i < strings; i++) {
				this.text.add(text.substring(lengthOfEachSegment * i, (lengthOfEachSegment * i) + lengthOfEachSegment));
			}
			this.setHeight(strings * 30);
		}
	}

	/**
	 * Gets the text of the String Component.
	 * 
	 * @return Text of the String Component as a string.
	 */
	
	public String getText() {
		return this.originalText;
	}

	@Override
	public void update() {

	}

	@Override
	public void OnParentWidthChanged() {
		setText(originalText);
	}

	@Override
	public void OnFontChanged(FontChangedEvent event) {
		setText(this.originalText);
	}
}