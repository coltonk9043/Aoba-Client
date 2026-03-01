/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.font;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.logging.LogUtils;

import net.aoba.Aoba;
import net.aoba.event.events.FontChangedEvent;
import net.aoba.managers.SettingManager;
import net.aoba.settings.types.StringSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.FontOption.Filter;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.GlyphStitcher;
import net.minecraft.client.gui.font.glyphs.EffectGlyph;
import net.minecraft.client.gui.font.providers.FreeTypeUtil;
import net.minecraft.client.gui.font.providers.TrueTypeGlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.TrueTypeGlyphProviderDefinition.Shift;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

public class FontManager {
	private final Minecraft MC;
	private Font currentFontRenderer;

	public ConcurrentHashMap<String, Font> fontRenderers;
	public StringSetting fontSetting;

	public FontManager() {
		fontRenderers = new ConcurrentHashMap<>();
		MC = Minecraft.getInstance();

		fontSetting = StringSetting.builder().id("aoba_font").displayName("Font")
				.description("The font that Aoba will use.").defaultValue("minecraft").build();

		fontSetting.addOnUpdate((i) -> {
			FontManager font = Aoba.getInstance().fontManager;
			font.SetRenderer(font.fontRenderers.get(i));
			LogUtils.getLogger().info("Changed font to " + i);
		});

		SettingManager.registerSetting(fontSetting);
	}

	public void Initialize() {
		fontRenderers.put("minecraft", MC.font);

		File fontDirectory = new File(MC.gameDirectory + File.separator + "aoba" + File.separator + "fonts");

		if (fontDirectory.exists() && fontDirectory.isDirectory()) {
			LogUtils.getLogger().info("Found Font Directory: " + fontDirectory.getAbsolutePath());
			File[] files = fontDirectory.listFiles((dir, name) -> name.endsWith(".ttf"));

			if (files != null) {
				for (File file : files) {
					try {
						GlyphProvider font = LoadTTFFont(file, 9f, 2, new TrueTypeGlyphProviderDefinition.Shift(-1, 0), "");
						List<GlyphProvider.Conditional> list = new ArrayList<>();
						list.add(new GlyphProvider.Conditional(font, Filter.ALWAYS_PASS));
						LogUtils.getLogger().info("Loading font " + file.getName());

						GlyphStitcher glyphStitcher = new GlyphStitcher(MC.getTextureManager(),
								Identifier.parse("aoba:fonts/" + file.getName()));
						FontSet storage = new FontSet(glyphStitcher);
						storage.reload(list, Set.of());

						fontRenderers.put(file.getName().replace(".ttf", ""),
								new Font(new Font.Provider() {
									@Override
									public GlyphSource glyphs(FontDescription desc) {
										return storage.source(false);
									}

									@Override
									public EffectGlyph effect() {
										return storage.whiteGlyph();
									}
								}));
					} catch (Exception e) {
						System.err.println("Failed to load font: " + file.getName());
						LogUtils.getLogger().error(e.getMessage());
					}
				}
			}
		}

		currentFontRenderer = fontRenderers.values().iterator().next();
	}

	public Font GetRenderer() {
		return currentFontRenderer;
	}

	public void SetRenderer(Font renderer) {
		currentFontRenderer = renderer;
		Aoba.getInstance().eventManager.Fire(new FontChangedEvent());
	}

	private static GlyphProvider LoadTTFFont(File location, float size, float oversample, Shift shift, String skip)
			throws IOException {
		ByteBuffer byteBuffer = MemoryUtil.memAlloc(Files.readAllBytes(location.toPath()).length);
		byteBuffer.put(Files.readAllBytes(location.toPath())).flip();

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
			FreeTypeUtil.assertError(
					FreeType.FT_New_Memory_Face(FreeTypeUtil.getLibrary(), byteBuffer, 0L, pointerBuffer),
					"Initializing font face");
			FT_Face fT_Face = FT_Face.create(pointerBuffer.get());

			String string = FreeType.FT_Get_Font_Format(fT_Face);
			if (!"TrueType".equals(string)) {
				throw new IOException("Font is not in TTF format, was " + string);
			}
			FreeTypeUtil.assertError(FreeType.FT_Select_Charmap(fT_Face, FreeType.FT_ENCODING_UNICODE),
					"Find unicode charmap");

			return new TrueTypeGlyphProvider(byteBuffer, fT_Face, size, oversample, shift.x(), shift.y(), skip);
		} finally {
			MemoryUtil.memFree(byteBuffer);
		}
	}
}
