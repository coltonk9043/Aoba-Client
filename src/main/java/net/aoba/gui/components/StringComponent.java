/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.ArrayList;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.Size;
import net.aoba.gui.types.TextAlign;
import net.aoba.gui.types.TextWrapping;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;
import net.minecraft.client.gui.Font;

public class StringComponent extends Component {
	private final ArrayList<String> lines = new ArrayList<String>();

	public static final UIProperty<String> TextProperty = new UIProperty<>("Text", "", false, true);
	public static final UIProperty<TextAlign> TextAlignmentProperty = new UIProperty<>("TextAlignment", TextAlign.Left, false, true);
	public static final UIProperty<TextWrapping> TextWrappingProperty = new UIProperty<>("TextWrapping", TextWrapping.Wrap, false, true);
	public static final UIProperty<Float> FontSizeProperty = new UIProperty<>("FontSize", 12f, true, true);
	public StringComponent() {
	}

	public StringComponent(String text) {
		setProperty(StringComponent.TextProperty, text);
	}

	private float getScale() {
		return 1.0f;
	}

	private float getLineHeight() {
		return Math.round(getProperty(StringComponent.FontSizeProperty) * 1.5f);
	}

	@Override
	public Size measure(Size availableSize) {
		recalculateLines(availableSize);

		Font textRenderer = getProperty(UIElement.FontProperty)
				.getRenderer(getProperty(StringComponent.FontSizeProperty), getProperty(UIElement.FontWeightProperty));
		String originalText = getProperty(StringComponent.TextProperty);
		float lineHeight = getLineHeight();
		float scale = getScale();

		TextWrapping textWrapping = getProperty(StringComponent.TextWrappingProperty);
		if (textWrapping == TextWrapping.NoWrap) {
			float fullWidth = originalText != null ? textRenderer.width(originalText) * scale : 0f;
			return new Size(fullWidth, lineHeight);
		}

		float maxLineWidth = 0;
		for (String line : lines) {
			float lineWidth = textRenderer.width(line) * scale;
			if (lineWidth > maxLineWidth)
				maxLineWidth = lineWidth;
		}

		float width = Math.min(maxLineWidth, availableSize.width());
		float height = lines.size() * lineHeight;
		return new Size(width, height);
	}

	public void draw(Renderer2D renderer, float partialTicks) {
		Rectangle actualRect = getActualSize();
		float actualX = actualRect.x();
		float actualY = actualRect.y();
		float actualWidth = actualRect.width();
		float lineHeight = getLineHeight();
		float fontSize = getProperty(StringComponent.FontSizeProperty);

		float y = 0;
		
		// TODO: Hacky...
		float drawOffset = (fontSize - 6f) * 7f / 6f;

		Shader fgEffect = getProperty(ForegroundProperty);
		Font font = getProperty(UIElement.FontProperty)
				.getRenderer(getProperty(StringComponent.FontSizeProperty),getProperty(UIElement.FontWeightProperty));

		TextAlign textAlignment = getProperty(StringComponent.TextAlignmentProperty);
		for (String str : lines) {
			float lineWidth = Renderer2D.getStringWidth(str, font, fontSize);
			float drawX = switch (textAlignment) {
				case Center -> actualX + (actualWidth - lineWidth) / 2.0f;
				case Right -> actualX + actualWidth - lineWidth;
				default -> actualX;
			};

			renderer.drawString(str, drawX, actualY + y + drawOffset, fgEffect, font, fontSize);

			y += lineHeight;
		}
	}

	public void recalculateLines(Size availableSize) {
		lines.clear();

		String originalText = getProperty(StringComponent.TextProperty);
		if (originalText != null) {
			Font textRenderer = getProperty(UIElement.FontProperty)
					.getRenderer(getProperty(StringComponent.FontSizeProperty), getProperty(UIElement.FontWeightProperty));
			float scale = getScale();

			float width = availableSize.width();
			float textWidth = textRenderer.width(originalText) * scale;

			TextWrapping textWrapping = getProperty(StringComponent.TextWrappingProperty);
			if (textWrapping == TextWrapping.NoWrap) {
				if (textWidth <= width) {
					lines.add(originalText);
				} else {
					String ellipsis = "...";
					float ellipsisWidth = textRenderer.width(ellipsis) * scale;
					StringBuilder buffer = new StringBuilder();
					for (int i = 0; i < originalText.length(); i++) {
						buffer.append(originalText.charAt(i));
						float bufferWidth = textRenderer.width(buffer.toString()) * scale;
						if (bufferWidth + ellipsisWidth >= width) {
							buffer.setLength(buffer.length() - 1);
							buffer.append(ellipsis);
							break;
						}
					}
					lines.add(buffer.toString());
				}
				return;
			}

			if (textWidth <= width) {
				lines.add(originalText);
			} else {
				// Since there are multiple lines, we will want to split them in a spot that
				// makes the most sense.
				StringBuilder buffer = new StringBuilder();
				int lastSplit = 0;
				int lastSpace = -1;
				for (int i = 0; i < originalText.length();) {
					char c = originalText.charAt(i);
					buffer.append(c);

					float wordBufferWidth = textRenderer.width(buffer.toString()) * scale;
					if (wordBufferWidth >= width) {
						if (lastSplit == -1)
							break;

						if (lastSpace == -1) {
							lines.add(originalText.substring(lastSplit));
							lastSplit = i - 1;
							++i;
						} else {
							lines.add(originalText.substring(lastSplit, lastSpace));
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
					lines.add(originalText.substring(lastSplit));
			}
		}
	}


	@Override
	public void update() {

	}
}