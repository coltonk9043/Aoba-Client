package net.aoba.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.IOUtils;
import com.google.common.base.Splitter;
import net.aoba.module.Module;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class Settings {
	private File aobaOptions;
	private MinecraftClient mc;
	private AobaClient aoba;

	private static Hashtable<String, String> settings = new Hashtable<String, String>();

	public Settings() {
		mc = MinecraftClient.getInstance();
		aoba = Aoba.getInstance();
		aobaOptions = new File(mc.runDirectory, "aoba_options.txt");
		readSettings();
	}

	public void saveSettings() {
		PrintWriter printwriter = null;
		try {
			printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(aobaOptions), StandardCharsets.UTF_8));

			// Write HUD information and 'other' settings.
			printwriter.println("x:" + aoba.hm.hud.getX());
			printwriter.println("y:" + aoba.hm.hud.getY());
			printwriter.println("color_hue:" + aoba.hm.getOriginalColor().hue);
			printwriter.println("rainbowUI:" + aoba.hm.rainbow.getValue());
			printwriter.println("armor_x:" + aoba.hm.armorHud.getX());
			printwriter.println("armor_y:" + aoba.hm.armorHud.getY());
			
			// Write Module Settings
			for (Module module : aoba.mm.modules) {
				for (Setting setting : module.getSettings()) {
					if(setting instanceof BooleanSetting) {
						BooleanSetting bs = (BooleanSetting)setting;
						printwriter.println(bs.getLine() + ":" + bs.getValue());
					}else if (setting instanceof SliderSetting) {
						SliderSetting ss = (SliderSetting)setting;
						printwriter.println(ss.getLine() + ":" + ss.getValue());
					}
					
				}
			}
			
			// Write Module Keybinds
			for (Module module : aoba.mm.modules) {
				printwriter.println(
						module.getBind().getCategory() + ":" + module.getBind().getBoundKeyTranslationKey());
			}
		} catch (Exception exception) {
			System.out.println("[Aoba] Failed to save settings");
		} finally {
			IOUtils.closeQuietly((Writer) printwriter);
		}
	}

	public void readSettings() {
		final Splitter COLON_SPLITTER = Splitter.on(':');
		try {
			if (!this.aobaOptions.exists()) {
				return;
			}
			List<String> list = IOUtils.readLines(new FileInputStream(this.aobaOptions), StandardCharsets.UTF_8);
			for (String s : list) {
				try {
					Iterator<String> iterator = COLON_SPLITTER.limit(2).split(s).iterator();
					settings.put(iterator.next(), iterator.next());
				} catch (Exception var10) {
					System.out.println("Skipping bad option: " + (Object) s);
				}
			}
			//KeyBinding.updateKeysByCode();
		} catch (Exception exception) {
			System.out.println("[Aoba] Failed to load settings");
		}
	}
	
	public static int getSettingInt(String setting) {
		String s = settings.get(setting);
		if(s == null) return 0;
		return Integer.parseInt(s);
	}
	
	public static float getSettingFloat(String setting) {
		String s = settings.get(setting);
		return Float.parseFloat(s);
	}
	
	public static boolean getSettingBoolean(String setting) {
		String s = settings.get(setting);
		return Boolean.parseBoolean(s);
	}
	
	public static String getSettingString(String setting) {
		return settings.get(setting);
	}
}
