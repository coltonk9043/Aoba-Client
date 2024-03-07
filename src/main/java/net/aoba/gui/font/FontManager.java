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

package net.aoba.gui.font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Struct;

import net.aoba.Aoba;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TrueTypeFont;
import net.minecraft.client.font.TrueTypeFontLoader;
import net.minecraft.client.font.TrueTypeFontLoader.Shift;
import net.minecraft.util.Identifier;

public class FontManager {
	private MinecraftClient MC;
	private TextRenderer currentFontRenderer;

	public HashMap<String, TextRenderer> fontRenderers;
	public StringSetting fontSetting;
	
	public FontManager() {
		fontRenderers = new HashMap<String, TextRenderer>();
		MC = MinecraftClient.getInstance();
		
		fontSetting = new StringSetting("font", "The font that Aoba will use.", "minecraft");
		fontSetting.setOnUpdate((i) -> {
			FontManager font = Aoba.getInstance().fontManager;
			font.SetRenderer(font.fontRenderers.get(i));
		});
		
		SettingManager.registerSetting(fontSetting, Aoba.getInstance().settingManager.hidden_category);
	}

	public void Initialize() {
		File fontDirectory = new File(MC.runDirectory + "\\aoba\\fonts\\");
		if (fontDirectory.isDirectory()) {
			File[] files = fontDirectory.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".ttf");
				}
			});

			fontRenderers.put("minecraft", MC.textRenderer);
			for (File file : files) {
				List<Font> list = new ArrayList<Font>();

				try {
					Font font = LoadTTFFont(file, 14, 2, new TrueTypeFontLoader.Shift(-1, -1f), "");
					list.add(font);
				} catch (Exception e) {
					e.printStackTrace();
				}

				FontStorage storage = new FontStorage(MC.getTextureManager(), new Identifier("aoba:" + file.getName()));
				storage.setFonts(list);
				fontRenderers.put(file.getName().replace(".ttf", ""), new TextRenderer(id -> storage, true));
			}
		}

		currentFontRenderer = fontRenderers.values().iterator().next();
	}

	public TextRenderer GetRenderer() {
		return currentFontRenderer;
	}

	public void SetRenderer(TextRenderer renderer) {
		this.currentFontRenderer = renderer;
	}

	private static Font LoadTTFFont(File location, float size, float oversample, Shift shift, String skip) throws IOException {
		TrueTypeFont trueTypeFont = null;
		Struct sTBTTFontinfo = null;
		ByteBuffer byteBuffer = null;

		InputStream inputStream = new FileInputStream(location);
		try {
			sTBTTFontinfo = STBTTFontinfo.malloc();
			byte[] test = inputStream.readAllBytes();
			byteBuffer = ByteBuffer.allocateDirect(test.length);
			byteBuffer.put(test, 0, test.length);
			byteBuffer.flip();

			if (!STBTruetype.stbtt_InitFont((STBTTFontinfo) sTBTTFontinfo, byteBuffer)) {
				inputStream.close();
				throw new IOException("Invalid ttf");
			}
			trueTypeFont = new TrueTypeFont(byteBuffer, (STBTTFontinfo) sTBTTFontinfo, size, oversample, shift.x(),
					shift.y(), skip);
		} catch (Throwable throwable) {
			try {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}
				}
				throw throwable;
			} catch (Exception exception) {
				if (sTBTTFontinfo != null) {
					sTBTTFontinfo.free();
				}
				MemoryUtil.memFree(byteBuffer);
				throw exception;
			}
		}
		inputStream.close();

		return trueTypeFont;
	}
}
