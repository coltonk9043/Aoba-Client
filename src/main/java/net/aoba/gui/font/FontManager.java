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

import com.mojang.logging.LogUtils;

import net.aoba.Aoba;
import net.aoba.event.events.FontChangedEvent;
import net.aoba.managers.SettingManager;
import net.aoba.settings.types.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType.FilterMap;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TrueTypeFont;
import net.minecraft.client.font.TrueTypeFontLoader;
import net.minecraft.client.font.TrueTypeFontLoader.Shift;
import net.minecraft.util.Identifier;

public class FontManager {
	private final MinecraftClient MC;
	private TextRenderer currentFontRenderer;

	public ConcurrentHashMap<String, TextRenderer> fontRenderers;
	public StringSetting fontSetting;

	public FontManager() {
		fontRenderers = new ConcurrentHashMap<>();
		MC = MinecraftClient.getInstance();

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
		fontRenderers.put("minecraft", MC.textRenderer);

		File fontDirectory = new File(MC.runDirectory + File.separator + "aoba" + File.separator + "fonts");

		if (fontDirectory.exists() && fontDirectory.isDirectory()) {
			LogUtils.getLogger().info("Found Font Directory: " + fontDirectory.getAbsolutePath());
			File[] files = fontDirectory.listFiles((dir, name) -> name.endsWith(".ttf"));

			if (files != null) {
				for (File file : files) {
					try {
						Font font = LoadTTFFont(file, 9f, 2, new TrueTypeFontLoader.Shift(-1, 0), "");
						List<Font.FontFilterPair> list = new ArrayList<>();
						list.add(new Font.FontFilterPair(font, FilterMap.NO_FILTER));
						LogUtils.getLogger().info("Loading font " + file.getName());

						try (FontStorage storage = new FontStorage(MC.getTextureManager(),
								Identifier.of("aoba:fonts/" + file.getName()))) {
							storage.setFonts(list, Set.of());
							fontRenderers.put(file.getName().replace(".ttf", ""),
									new TextRenderer(id -> storage, true));
						}
					} catch (Exception e) {
						System.err.println("Failed to load font: " + file.getName());
						LogUtils.getLogger().error(e.getMessage());
					}
				}
			}
		}

		currentFontRenderer = fontRenderers.values().iterator().next();
	}

	public TextRenderer GetRenderer() {
		return currentFontRenderer;
	}

	public void SetRenderer(TextRenderer renderer) {
		currentFontRenderer = renderer;
		Aoba.getInstance().eventManager.Fire(new FontChangedEvent());
	}

	private static Font LoadTTFFont(File location, float size, float oversample, Shift shift, String skip)
			throws IOException {
		ByteBuffer byteBuffer = MemoryUtil.memAlloc(Files.readAllBytes(location.toPath()).length);
		byteBuffer.put(Files.readAllBytes(location.toPath())).flip();

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
			FreeTypeUtil.checkFatalError(
					FreeType.FT_New_Memory_Face(FreeTypeUtil.initialize(), byteBuffer, 0L, pointerBuffer),
					"Initializing font face");
			FT_Face fT_Face = FT_Face.create(pointerBuffer.get());

			String string = FreeType.FT_Get_Font_Format(fT_Face);
			if (!"TrueType".equals(string)) {
				throw new IOException("Font is not in TTF format, was " + string);
			}
			FreeTypeUtil.checkFatalError(FreeType.FT_Select_Charmap(fT_Face, FreeType.FT_ENCODING_UNICODE),
					"Find unicode charmap");

			return new TrueTypeFont(byteBuffer, fT_Face, size, oversample, shift.x(), shift.y(), skip);
		} finally {
			MemoryUtil.memFree(byteBuffer);
		}
	}
}
