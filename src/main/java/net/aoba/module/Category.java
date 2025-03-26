/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.Identifier;

public class Category {
	private static final Map<String, Category> CATEGORIES = new HashMap<>();

	private final String name;
	private final Identifier icon;

	private Category(String name) {
		this.name = name;
		icon = Identifier.of("aoba", "textures/" + name.toLowerCase() + ".png");
	}

	public Category(String name, Identifier icon) {
		this.name = name;
		this.icon = icon;
	}

	public static Category of(String name) {
		String capitalized = name.toLowerCase();
		if (name.length() >= 2)
			capitalized = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		return CATEGORIES.computeIfAbsent(capitalized, Category::new);
	}

	public String getName() {
		return name;
	}

	public Identifier getIcon() {
		return icon;
	}

	public static Map<String, Category> getAllCategories() {
		return CATEGORIES;
	}
}