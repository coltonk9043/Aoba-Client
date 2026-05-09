/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.font;

import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gui.Font;

public class UIFont {
	private final String name;
	private final byte[] fontBytes;
	private final ConcurrentHashMap<Long, Font> bakedFonts = new ConcurrentHashMap<>();

	public UIFont(String name, byte[] fontBytes) {
		this.name = name;
		this.fontBytes = fontBytes;
		getRenderer(FontManager.DEFAULT_FONT_SIZE, FontManager.WEIGHT_NORMAL);
	}

	public String getName() {
		return name;
	}

	public Font getRenderer() {
		return getRenderer(FontManager.DEFAULT_FONT_SIZE, FontManager.WEIGHT_NORMAL);
	}

	public Font getRenderer( float fontSize, int weight) {
		float atlasSize = Math.round(fontSize * 1.5f);
		
		int snappedWeight = Math.max(100, Math.min(900, Math.round(weight / 100f) * 100));
		long key = makeKey(atlasSize, snappedWeight);

		Font existing = bakedFonts.get(key);
		if (existing != null)
			return existing;

		if (fontBytes == null)
			return getRenderer(FontManager.DEFAULT_FONT_SIZE, FontManager.WEIGHT_NORMAL);

		Font baked = bakedFonts.computeIfAbsent(key, _ -> {
			float embolden = FontManager.getEmboldenForWeight(snappedWeight);
			try {
				String fontId = name + "_w" + snappedWeight + "_s" + Math.round(atlasSize);
				Font font = FontManager.loadFontFromBytes(fontBytes, fontId, embolden, atlasSize);
				return font;
			} catch (Exception e) {
				return null;
			}
		});

		return baked != null ? baked : getRenderer(FontManager.DEFAULT_FONT_SIZE, FontManager.WEIGHT_NORMAL);
	}

	private static long makeKey(float atlasSize, int weight) {
		return ((long) weight << 32) | (Float.floatToIntBits(atlasSize) & 0xFFFFFFFFL);
	}
}
