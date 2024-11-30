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

package net.aoba.gui.components;

import java.util.ArrayList;

import net.aoba.Aoba;
import net.aoba.event.events.FontChangedEvent;
import net.aoba.event.listeners.FontChangedListener;
import net.aoba.gui.Margin;
import net.aoba.gui.Size;
import net.aoba.gui.TextAlign;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class StringComponent extends Component implements FontChangedListener {
	private TextAlign textAlign = TextAlign.Left;
	private String originalText;
	private ArrayList<String> text = new ArrayList<String>();
	private boolean bold;
	private Color color;

	public StringComponent(String text) {
		super();
		setText(text);
		this.color = Colors.White;
		this.bold = false;
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.setIsHitTestVisible(false);
		Aoba.getInstance().eventManager.AddListener(FontChangedListener.class, this);
	}

	public StringComponent(String text, boolean bold) {
		super();
		setText(text);
		this.color = Colors.White;
		this.bold = bold;
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.setIsHitTestVisible(false);
		Aoba.getInstance().eventManager.AddListener(FontChangedListener.class, this);
	}

	public StringComponent(String text, Color color, boolean bold) {
		super();
		setText(text);
		this.color = color;
		this.bold = bold;
		this.setMargin(new Margin(8f, 2f, 8f, 2f));
		this.setIsHitTestVisible(false);
		Aoba.getInstance().eventManager.AddListener(FontChangedListener.class, this);
	}

	@Override
	public void measure(Size availableSize) {
		recalculateLines(availableSize);
		preferredSize = new Size(availableSize.getWidth(), text.size() * 28f);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		float actualX = this.getActualSize().getX();
		float actualY = this.getActualSize().getY();
		float actualWidth = this.getActualSize().getWidth();

		int i = 5;

		for (String str : text) {
			if (bold)
				str = Formatting.BOLD + str;

			switch (textAlign) {
			case TextAlign.Left:
				Render2D.drawString(drawContext, str, actualX, actualY + i, this.color.getColorAsInt());
				break;
			case TextAlign.Center:
				float xPosCenter = actualX + (actualWidth / 2.0f) - Render2D.getStringWidth(str);
				Render2D.drawString(drawContext, str, xPosCenter, actualY + i, this.color.getColorAsInt());
				break;
			case TextAlign.Right:
				float xPosRight = actualX + actualWidth - (Render2D.getStringWidth(str) * 2);
				Render2D.drawString(drawContext, str, xPosRight, actualY + i, this.color.getColorAsInt());
				break;
			}

			i += 25;
		}
	}

	/**
	 * Sets the text of the String Component.
	 *
	 * @param text The text to set.
	 */
	public void setText(String text) {
		if (actualSize != null) {
			this.originalText = text;
			invalidateMeasure();
		}
	}

	public void recalculateLines(Size availableSize) {
		this.text.clear();

		if (originalText != null) {
			TextRenderer textRenderer = Aoba.getInstance().fontManager.GetRenderer();

			float width = availableSize.getWidth().floatValue();
			float textWidth = textRenderer.getWidth(originalText) * 2.0f;
			if (textWidth < width) {
				this.text.add(originalText);
			} else {
				// Single there are multiple lines, we will want to split them in a spot that
				// makes the most sense.
				StringBuilder buffer = new StringBuilder();
				int lastSplit = 0;
				int lastSpace = -1;
				for (int i = 0; i < originalText.length();) {
					char c = originalText.charAt(i);
					buffer.append(c);

					float wordBufferWidth = textRenderer.getWidth(buffer.toString()) * 2.0f;
					if (wordBufferWidth >= width) {
						if (lastSplit == -1)
							break;

						if (lastSpace == -1) {
							this.text.add(originalText.substring(lastSplit));
							lastSplit = i - 1;
							++i;
						} else {
							this.text.add(originalText.substring(lastSplit, lastSpace));
							lastSplit = lastSpace + 1;
							i = lastSplit;
							lastSpace = -1;
						}
						buffer.setLength(0);
						continue;
					}

					if (c == ' ') {
						lastSpace = i;
					}
					++i;
				}

				if (lastSplit != -1 && lastSplit < originalText.length())
					this.text.add(originalText.substring(lastSplit));
			}
		}
	}

	public TextAlign getTextAlign() {
		return this.textAlign;
	}

	public void setTextAlign(TextAlign textAlign) {
		this.textAlign = textAlign;
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
	public void onFontChanged(FontChangedEvent event) {
		setText(this.originalText);
	}
}