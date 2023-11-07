package net.aoba.settings;

import net.aoba.core.utils.types.Vector2;
import net.aoba.gui.Color;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.IntegerSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SettingManager {
	private static boolean DEBUG_STUFF = false;

	public List<Setting<?>> config_category = new ArrayList<>();
	public List<Setting<?>> modules_category = new ArrayList<>();
	public List<Setting<?>> hidden_category = new ArrayList<>();

	public static void register_setting(Setting<?> p_setting, List<Setting<?>> p_category) {
		p_category.add(p_setting);
	}

	public static Setting<?> get_setting_in_category(String p_setting_id, List<Setting<?>> p_category) {
		for (Setting<?> setting : p_category) {
			if (setting.ID.equals(p_setting_id)) {
				return setting;
			}
		}
		return null;
	}

	public static File configFolder;
	public static File configFile;
	public static Properties config;

	public static void prepare(String name) {
		try {
			configFolder = new File(MinecraftClient.getInstance().runDirectory + File.separator + "aoba");
			configFile = new File(configFolder + File.separator + name + ".xml");
			
			if (!configFolder.exists())
				configFolder.mkdirs();
			
			if (!configFile.exists())
				configFile.createNewFile();
			
			config = new Properties();
		} catch (Exception ignored) {
			
		}
	}

	public static void saveSettings(String name, List<Setting<?>> config_category2) {
		try {
			System.out.println("Saving config " + name + ".");
			prepare(name);
			for (Setting setting : config_category2) {
				switch (setting.type) {
				case DOUBLE, INTEGER, BOOLEAN, STRING -> {
					config.setProperty(setting.ID, String.valueOf(setting.getValue()));
				}
				case KEYBIND -> {
					Key key = InputUtil
							.fromTranslationKey(((KeyBinding) setting.getValue()).getBoundKeyTranslationKey());
					config.setProperty(setting.ID, String.valueOf(key.getCode()));
				}
				case VECTOR2 -> {
					config.setProperty(setting.ID + "_x", String.valueOf(((Vector2) setting.getValue()).x));
					config.setProperty(setting.ID + "_y", String.valueOf(((Vector2) setting.getValue()).y));
				}
				case COLOR -> {
					config.setProperty(setting.ID, String.valueOf(((Color) setting.getValue()).getColorAsInt()));
				}
				}
			}
			config.storeToXML(new FileOutputStream(configFile), null);
		} catch (Exception ignored) {
		}
	}

	public static void loadSettings(String name, List<Setting<?>> config_category2) {
		try {
			System.out.println("Loading config " + name + ".");
			prepare(name);
			config.loadFromXML(new FileInputStream(configFile));
			for (Setting setting : config_category2) {

				String value = config.getProperty(setting.ID, null);
				
				if (DEBUG_STUFF)System.out.println(setting.displayName + " " + setting.value + " " + Double.parseDouble(value));
				
				if (value == null)
					break;

				switch (setting.type) {
				case DOUBLE -> {
					float floatValue = Float.parseFloat(value);
					if (((FloatSetting) setting).min_value <= floatValue
							&& ((FloatSetting) setting).max_value >= floatValue) {
						setting.setValue(Double.parseDouble(value));
					}
				}
				case INTEGER -> {
					int intValue = Integer.parseInt(value);
					if (((IntegerSetting) setting).min_value <= intValue && ((IntegerSetting) setting).max_value >= intValue) {
						setting.setValue(Integer.parseInt(value));
					}
				}
				case BOOLEAN -> {
					setting.setValue(Boolean.parseBoolean(value));
				}
				case STRING -> {
					setting.setValue(value);
				}
				case KEYBIND -> {
					int keyCode = Integer.parseInt(config.getProperty(setting.ID, null));
					setting.setValue(InputUtil.fromKeyCode(keyCode, 0));
				}
				case VECTOR2 -> {
					String value_x = config.getProperty(setting.ID + "_x", null);
					String value_y = config.getProperty(setting.ID + "_y", null);
					if (value_x == null || value_y == null)
						break;
					setting.setValue(new Vector2(Float.parseFloat(value_x), Float.parseFloat(value_y)));
				}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public enum SettingCategories {
		CONFIG, MODULES, HIDDEN
	}
}
