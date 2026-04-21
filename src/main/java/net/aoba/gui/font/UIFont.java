/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.font;

import java.util.concurrent.ConcurrentHashMap;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.Font;

public class UIFont {
	private final String name;
	private final byte[] fontBytes;
	private final ConcurrentHashMap<Integer, Font> weights = new ConcurrentHashMap<>();

	public UIFont(String name, Font normalWeight, byte[] fontBytes) {
		this.name = name;
		this.fontBytes = fontBytes;
		weights.put(FontManager.WEIGHT_NORMAL, normalWeight);
	}

	public String getName() {
		return name;
	}

	public Font getRenderer() {
		return getRenderer(FontManager.WEIGHT_NORMAL);
	}

	public Font getRenderer(int weight) {
		weight = snapWeight(weight);

		Font font = weights.get(weight);
		if (font != null) 
			return font;

		if (fontBytes == null) 
			return weights.get(FontManager.WEIGHT_NORMAL);

		float embolden = FontManager.getEmboldenForWeight(weight);
		try {
			font = FontManager.loadFontFromBytes(fontBytes, name + "_w" + weight, embolden);
			weights.put(weight, font);
			LogUtils.getLogger().info("Created weight " + weight + " for font: " + name);
			return font;
		} catch (Exception e) {
			LogUtils.getLogger().error("Failed to create weight " + weight + " for font: " + name, e);
			return weights.get(FontManager.WEIGHT_NORMAL);
		}
	}

	private static int snapWeight(int weight) {
		weight = Math.round(weight / 100f) * 100;
		return Math.max(100, Math.min(900, weight));
	}
}
