/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
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
	private final ArrayList<String> text = new ArrayList<String>();
	private final boolean bold;
	private final Color color;

	public StringComponent(String text) {
        setText(text);
		color = Colors.White;
		bold = false;
		setMargin(new Margin(8f, 2f, 8f, 2f));
		setIsHitTestVisible(false);
		Aoba.getInstance().eventManager.AddListener(FontChangedListener.class, this);
	}

	public StringComponent(String text, boolean bold) {
        setText(text);
		color = Colors.White;
		this.bold = bold;
		setMargin(new Margin(8f, 2f, 8f, 2f));
		setIsHitTestVisible(false);
		Aoba.getInstance().eventManager.AddListener(FontChangedListener.class, this);
	}

	public StringComponent(String text, Color color, boolean bold) {
        setText(text);
		this.color = color;
		this.bold = bold;
		setMargin(new Margin(8f, 2f, 8f, 2f));
		setIsHitTestVisible(false);
		Aoba.getInstance().eventManager.AddListener(FontChangedListener.class, this);
	}

	@Override
	public void measure(Size availableSize) {
		recalculateLines(availableSize);
		preferredSize = new Size(availableSize.getWidth(), text.size() * 28f);
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();

		int i = 5;

		for (String str : text) {
			if (bold)
				str = Formatting.BOLD + str;

			switch (textAlign) {
			case Left:
				Render2D.drawString(drawContext, str, actualX, actualY + i, color.getColorAsInt());
				break;
			case Center:
				float xPosCenter = actualX + (actualWidth / 2.0f) - Render2D.getStringWidth(str);
				Render2D.drawString(drawContext, str, xPosCenter, actualY + i, color.getColorAsInt());
				break;
			case Right:
				float xPosRight = actualX + actualWidth - (Render2D.getStringWidth(str) * 2);
				Render2D.drawString(drawContext, str, xPosRight, actualY + i, color.getColorAsInt());
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
			originalText = text;
			invalidateMeasure();
		}
	}

	public void recalculateLines(Size availableSize) {
		text.clear();

		if (originalText != null) {
			TextRenderer textRenderer = Aoba.getInstance().fontManager.GetRenderer();

			float width = availableSize.getWidth().floatValue();
			float textWidth = textRenderer.getWidth(originalText) * 2.0f;
			if (textWidth < width) {
				text.add(originalText);
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
							text.add(originalText.substring(lastSplit));
							lastSplit = i - 1;
							++i;
						} else {
							text.add(originalText.substring(lastSplit, lastSpace));
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
					text.add(originalText.substring(lastSplit));
			}
		}
	}

	public TextAlign getTextAlign() {
		return textAlign;
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
		return originalText;
	}

	@Override
	public void update() {

	}

	@Override
	public void onFontChanged(FontChangedEvent event) {
		setText(originalText);
	}
}