/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import java.util.function.Consumer;

import net.aoba.Aoba;
import net.aoba.gui.font.FontManager;
import net.aoba.gui.font.UIFont;
import net.aoba.settings.Setting;

public class FontSetting extends Setting<UIFont> {
	private String fontName;

	protected FontSetting(String ID, String displayName, String description, String defaultFontName, Consumer<UIFont> onUpdate) {
		super(ID, displayName, description, Aoba.getInstance().fontManager.getDefaultFont(), onUpdate);
		this.fontName = defaultFontName;
		type = TYPE.FONT;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String name) {
		this.fontName = name;
		UIFont resolved = Aoba.getInstance().fontManager.getFont(name);
		super.setValue(resolved);
	}

	@Override
	protected boolean isValueValid(UIFont value) {
		return value != null;
	}

	public static FontSetting.BUILDER builder() {
		return new FontSetting.BUILDER();
	}

	public static class BUILDER extends Setting.BUILDER<FontSetting.BUILDER, FontSetting, UIFont> {
		private String fontName = FontManager.DEFAULT_FONT;

		protected BUILDER() {
		}

		public FontSetting.BUILDER fontName(String name) {
			this.fontName = name;
			return this;
		}

		@Override
		public FontSetting build() {
			return new FontSetting(id, displayName, description, fontName, onUpdate);
		}
	}
}
