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
import net.aoba.gui.Rectangle;
import net.aoba.gui.Size;
import net.aoba.gui.TextAlign;
import net.aoba.gui.TextWrapping;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.aoba.utils.render.Render2D;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class StringComponent extends Component implements FontChangedListener {
	private TextAlign textAlign = TextAlign.Left;
	private TextWrapping textWrapping = TextWrapping.Wrap;
	private String originalText;
	private final ArrayList<String> text = new ArrayList<String>();
	private final boolean bold;
	private Color color;

	public StringComponent(String text) {
		this(text, Colors.White, false);
	}

	public StringComponent(String text, boolean bold) {
		this(text, Colors.White, bold);
	}

	public StringComponent(String text, Color color, boolean bold) {
		setText(text);
		this.color = color;
		this.bold = bold;
		setIsHitTestVisible(false);
		Aoba.getInstance().eventManager.AddListener(FontChangedListener.class, this);
	}

	private float getLineHeight() {
		Font textRenderer = Aoba.getInstance().fontManager.GetRenderer();
		return textRenderer.lineHeight * 2.0f;
	}

	@Override
	public Size measure(Size availableSize) {
		recalculateLines(availableSize);

		Font textRenderer = Aoba.getInstance().fontManager.GetRenderer();
		float lineHeight = getLineHeight();

		if (textWrapping == TextWrapping.NoWrap) {
			float fullWidth = originalText != null ? textRenderer.width(originalText) * 2.0f : 0f;
			return new Size(fullWidth, lineHeight);
		}

		float maxLineWidth = 0;
		for (String line : text) {
			float lineWidth = textRenderer.width(line) * 2.0f;
			if (lineWidth > maxLineWidth)
				maxLineWidth = lineWidth;
		}

		float width = Math.min(maxLineWidth, availableSize.getWidth());
		float height = text.size() * lineHeight;
		return new Size(width, height);
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		Rectangle actualRect = getActualSize();
		float actualX = actualRect.getX();
		float actualY = actualRect.getY();
		float actualWidth = actualRect.getWidth();
		float lineHeight = getLineHeight();

		float y = 0;
		for (String str : text) {
			if (bold)
				str = ChatFormatting.BOLD + str;

			switch (textAlign) {
			case Left:
				Render2D.drawString(drawContext, str, actualX, actualY + y, color.getColorAsInt());
				break;
			case Center:
				float xPosCenter = actualX + (actualWidth / 2.0f) - Render2D.getStringWidth(str);
				Render2D.drawString(drawContext, str, xPosCenter, actualY + y, color.getColorAsInt());
				break;
			case Right:
				float xPosRight = actualX + actualWidth - (Render2D.getStringWidth(str) * 2);
				Render2D.drawString(drawContext, str, xPosRight, actualY + y, color.getColorAsInt());
				break;
			}

			y += lineHeight;
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
			Font textRenderer = Aoba.getInstance().fontManager.GetRenderer();

			float width = availableSize.getWidth().floatValue();
			float textWidth = textRenderer.width(originalText) * 2.0f;

			if (textWrapping == TextWrapping.NoWrap) {
				if (textWidth <= width) {
					text.add(originalText);
				} else {
					String ellipsis = "...";
					float ellipsisWidth = textRenderer.width(ellipsis) * 2.0f;
					StringBuilder buffer = new StringBuilder();
					for (int i = 0; i < originalText.length(); i++) {
						buffer.append(originalText.charAt(i));
						float bufferWidth = textRenderer.width(buffer.toString()) * 2.0f;
						if (bufferWidth + ellipsisWidth >= width) {
							buffer.setLength(buffer.length() - 1);
							buffer.append(ellipsis);
							break;
						}
					}
					text.add(buffer.toString());
				}
				return;
			}

			if (textWidth < width) {
				text.add(originalText);
			} else {
				// Since there are multiple lines, we will want to split them in a spot that
				// makes the most sense.
				StringBuilder buffer = new StringBuilder();
				int lastSplit = 0;
				int lastSpace = -1;
				for (int i = 0; i < originalText.length();) {
					char c = originalText.charAt(i);
					buffer.append(c);

					float wordBufferWidth = textRenderer.width(buffer.toString()) * 2.0f;
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

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public TextWrapping getTextWrapping() {
		return textWrapping;
	}

	public void setTextWrapping(TextWrapping textWrapping) {
		if (this.textWrapping != textWrapping) {
			this.textWrapping = textWrapping;
			invalidateMeasure();
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