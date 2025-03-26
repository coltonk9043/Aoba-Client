/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import com.mojang.logging.LogUtils;

import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.settings.Setting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.ColorSetting.ColorMode;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.IntegerSetting;
import net.aoba.settings.types.StringSetting;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class SettingManager {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private static final StringSetting currentConfig = StringSetting.builder().id("selected_config").defaultValue("default")
			.onUpdate(s -> {
				loadSettings();
			}).build();

	private final static HashSet<Setting<?>> globalSettings = new HashSet<>();
	private final static HashSet<Setting<?>> settings = new HashSet<>();
	public final static List<String> configNames = new ArrayList<String>();

	public SettingManager() {
		refreshSettingFiles();
	}

	/**
	 * Registers a setting that is a part of a settings profile.
	 * 
	 * @param setting Setting to add to the settings list.
	 */
	public static void registerSetting(Setting<?> setting) {
		settings.add(setting);
	}

	/**
	 * Registers a global setting that will not change regardless of settings
	 * profile.
	 * 
	 * @param setting Setting to add to global settings list.
	 */
	public static void registerGlobalSetting(Setting<?> setting) {
		globalSettings.add(setting);
	}

	public static void setCurrentConfig(String name) {
		if (configNames.contains(name)) {
			currentConfig.setValue(name);
		}
	}

	/**
	 * Rescans the %appdata%\.minecraft\aoba\settings directory for settings XML
	 * files.
	 */
	public static void refreshSettingFiles() {
		File settingsDirecotry = new File(MC.runDirectory + File.separator + "aoba" + File.separator + "settings");

		if (settingsDirecotry.exists() && settingsDirecotry.isDirectory()) {
			LogUtils.getLogger().info("Found Settings Directory: " + settingsDirecotry.getAbsolutePath());
			File[] files = settingsDirecotry.listFiles((dir, name) -> name.endsWith(".xml"));

			if (files != null) {
				for (File file : files) {
					String name = file.getName().replace(".xml", "");
					if (!name.equals("globals"))
						configNames.add(name);
				}
			}
		}
	}

	/**
	 * Saves the current settings profile to the disk (usually on shutdown)
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void saveSettings() throws FileNotFoundException, IOException {
		File settingsFolder = new File(MC.runDirectory + File.separator + "aoba" + File.separator + "settings");
		if (!settingsFolder.exists() && !settingsFolder.mkdirs()) {
			throw new IOException("Failed to create settings folder: " + settingsFolder.getAbsolutePath());
		} else {
			// Save Global config.
			LogUtils.getLogger().info("Saving global config.");
			Properties globalConfig = new Properties();
			fillProperties(globalConfig, globalSettings);
			globalConfig.storeToXML(new FileOutputStream(settingsFolder.getPath() + File.separator + "globals.xml"),
					null);

			// Save selectable config.
			String configName = currentConfig.getValue();
			LogUtils.getLogger().info("Saving config " + configName + ".");
			Properties config = new Properties();
			fillProperties(config, settings);
			config.storeToXML(new FileOutputStream(settingsFolder.getPath() + File.separator + configName + ".xml"),
					null);
		}
	}

	/**
	 * Saves a copy of the current settings profile to the disk.
	 * 
	 * @param fileName File name to save to.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void saveCopy(String fileName) throws FileNotFoundException, IOException {
		// Don't allow this
		if (fileName.equals("default") || fileName.equals("globals"))
			return;

		File settingsFolder = new File(MC.runDirectory + File.separator + "aoba" + File.separator + "settings");
		if (!settingsFolder.exists() && !settingsFolder.mkdirs()) {
			throw new IOException("Failed to create settings folder: " + settingsFolder.getAbsolutePath());
		} else {
			LogUtils.getLogger().info("Saving config " + fileName + ".");
			Properties config = new Properties();
			fillProperties(config, settings);
			config.storeToXML(new FileOutputStream(settingsFolder.getPath() + File.separator + fileName + ".xml"),
					null);
		}
	}

	private static void fillProperties(Properties properties, HashSet<Setting<?>> settings) {
		for (Setting<?> setting : settings) {
			try {
				switch (setting.type) {
				case FLOAT, INTEGER, BOOLEAN, STRING -> {
					properties.setProperty(setting.ID, String.valueOf(setting.getValue()));
				}
				case KEYBIND -> {
					Key key = ((Key) setting.getValue());
					properties.setProperty(setting.ID, String.valueOf(key.getCode()));
				}
				case RECTANGLE -> {
					properties.setProperty(setting.ID,
							((Rectangle) setting.getValue()).getX() + "," + ((Rectangle) setting.getValue()).getY()
									+ "," + ((Rectangle) setting.getValue()).getWidth() + ","
									+ ((Rectangle) setting.getValue()).getHeight());
				}
				case COLOR -> {
					ColorSetting cSetting = (ColorSetting) setting;
					String s = cSetting.getMode().name() + "," + ((Color) setting.getValue()).getColorAsHex();
					properties.setProperty(setting.ID, s);
				}
				case BLOCKS -> {
					@SuppressWarnings("unchecked")
					HashSet<Block> s = (HashSet<Block>) setting.getValue();
					StringBuilder result = new StringBuilder();

					int iteration = 0;
					for (Block block : s) {
						Identifier id = Registries.BLOCK.getId(block);
						result.append(id.getNamespace()).append(":").append(id.getPath());
						if (iteration != s.size() - 1) {
							result.append(",");
						}
						iteration++;
					}

					properties.setProperty(setting.ID, result.toString());
				}
				case ENUM -> {
					properties.setProperty(setting.ID, ((Enum<?>) setting.getValue()).name());
				}
				case VEC3D -> {
					Vec3d vec = (Vec3d) setting.getValue();
					properties.setProperty(setting.ID, vec.x + "," + vec.y + "," + vec.z);
				}
				default -> throw new IllegalArgumentException("Unexpected value: " + setting.type);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Loads the global settings from the globals.xml
	 */
	public static void loadGlobalSettings() {
		try {
			LogUtils.getLogger().info("Loading global config.");

			// Load config from file.
			Properties config = new Properties();
			try (FileInputStream fis = new FileInputStream(MC.runDirectory + File.separator + "aoba" + File.separator
					+ "settings" + File.separator + "globals.xml")) {
				config.loadFromXML(fis);
			} catch (InvalidPropertiesFormatException e) {
				LogUtils.getLogger().error("Invalid XML format in properties file: " + e.getMessage());
			} catch (IOException e) {
				LogUtils.getLogger().error("IOException while loading properties file: " + e.getMessage());
			}

			for (Setting setting : globalSettings) {
				try {
					String value = config.getProperty(setting.ID, null);
					if (value == null)
						break;

					switch (setting.type) {
					case FLOAT -> {
						FloatSetting floatSetting = (FloatSetting) setting;
						floatSetting.setValue(Float.parseFloat(value));
					}
					case INTEGER -> {
						IntegerSetting intSetting = (IntegerSetting) setting;
						intSetting.setValue(Integer.parseInt(value));
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
					case RECTANGLE -> {
						String[] dimensions = value.split(",");
						if (dimensions.length == 4) {
							Float x = dimensions[0].equals("null") ? null : Float.parseFloat(dimensions[0]);
							Float y = dimensions[1].equals("null") ? null : Float.parseFloat(dimensions[1]);
							Float width = dimensions[2].equals("null") ? null : Float.parseFloat(dimensions[2]);
							Float height = dimensions[3].equals("null") ? null : Float.parseFloat(dimensions[3]);

							setting.setValue(new Rectangle(x, y, width, height));
						}
					}
					case COLOR -> {
						String[] splits = value.split(",");
						ColorSetting cSetting = (ColorSetting) setting;
						if (splits.length == 2) {
							ColorMode enumValue = Enum.valueOf(((ColorSetting) setting).getMode().getDeclaringClass(),
									splits[0]);
							long hexValue = Long.parseLong(splits[1].replace("#", ""), 16);
							int Alpha = (int) ((hexValue) >> 24) & 0xFF;
							int R = (int) ((hexValue) >> 16) & 0xFF;
							int G = (int) ((hexValue) >> 8) & 0xFF;
							int B = (int) (hexValue) & 0xFF;

							cSetting.setMode(enumValue);
							if (enumValue == ColorMode.Solid) {
								setting.setValue(new Color(R, G, B, Alpha));
							}
						}
					}
					case BLOCKS -> {
						String[] ids = value.split(",");
						HashSet<Block> result = new HashSet<Block>();
						for (String str : ids) {
							Identifier i = Identifier.of(str);
							result.add(Registries.BLOCK.get(i));
						}
						setting.setValue(result);
					}
					case INDEXEDSTRINGLIST, STRINGLIST ->
						throw new UnsupportedOperationException("Unimplemented case: " + setting.type);
					case ENUM -> {
						String enumName = config.getProperty(setting.ID, null);
						if (enumName != null) {
							Enum<?> enumValue = Enum.valueOf((((Enum<?>) setting.getValue()).getDeclaringClass()),
									enumName);
							setting.setValue(enumValue);
						}
					}
					case VEC3D -> {
						String[] components = value.split(",");
						if (components.length == 3) {
							float x = Float.parseFloat(components[0]);
							float y = Float.parseFloat(components[1]);
							float z = Float.parseFloat(components[2]);
							setting.setValue(new Vec3d(x, y, z));
						}
					}
					default -> throw new IllegalArgumentException("Unexpected value: " + setting.type);
					}
				} catch (Exception e) {
					LogUtils.getLogger().error(e.getMessage());
				}
			}
		} catch (Exception e) {
			LogUtils.getLogger().error(e.getMessage());
		}
	}

	/**
	 * Loads the settings based off of the current configuration value.
	 */
	public static void loadSettings() {
		try {
			String configName = currentConfig.getValue();
			LogUtils.getLogger().info("Loading config " + configName + ".");

			// Load config from file.
			Properties config = new Properties();
			try (FileInputStream fis = new FileInputStream(MC.runDirectory + File.separator + "aoba" + File.separator
					+ "settings" + File.separator + configName + ".xml")) {
				config.loadFromXML(fis);
			} catch (InvalidPropertiesFormatException e) {
				LogUtils.getLogger().error("Invalid XML format in properties file: " + e.getMessage());
			} catch (IOException e) {
				LogUtils.getLogger().error("IOException while loading properties file: " + e.getMessage());
			}

			for (Setting setting : settings) {
				try {
					String value = config.getProperty(setting.ID, null);
					if (value == null)
						break;

					switch (setting.type) {
					case FLOAT -> {
						FloatSetting floatSetting = (FloatSetting) setting;
						floatSetting.setValue(Float.parseFloat(value));
					}
					case INTEGER -> {
						IntegerSetting intSetting = (IntegerSetting) setting;
						intSetting.setValue(Integer.parseInt(value));
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
					case RECTANGLE -> {
						String[] dimensions = value.split(",");
						if (dimensions.length == 4) {
							Float x = dimensions[0].equals("null") ? null : Float.parseFloat(dimensions[0]);
							Float y = dimensions[1].equals("null") ? null : Float.parseFloat(dimensions[1]);
							Float width = dimensions[2].equals("null") ? null : Float.parseFloat(dimensions[2]);
							Float height = dimensions[3].equals("null") ? null : Float.parseFloat(dimensions[3]);

							setting.setValue(new Rectangle(x, y, width, height));
						}
					}
					case COLOR -> {
						String[] splits = value.split(",");
						ColorSetting cSetting = (ColorSetting) setting;
						if (splits.length == 2) {
							ColorMode enumValue = Enum.valueOf(((ColorSetting) setting).getMode().getDeclaringClass(),
									splits[0]);
							long hexValue = Long.parseLong(splits[1].replace("#", ""), 16);
							int Alpha = (int) ((hexValue) >> 24) & 0xFF;
							int R = (int) ((hexValue) >> 16) & 0xFF;
							int G = (int) ((hexValue) >> 8) & 0xFF;
							int B = (int) (hexValue) & 0xFF;

							cSetting.setMode(enumValue);
							if (enumValue == ColorMode.Solid) {
								setting.setValue(new Color(R, G, B, Alpha));
							}
						}
					}
					case BLOCKS -> {
						String[] ids = value.split(",");
						HashSet<Block> result = new HashSet<Block>();
						for (String str : ids) {
							Identifier i = Identifier.of(str);
							result.add(Registries.BLOCK.get(i));
						}
						setting.setValue(result);
					}
					case INDEXEDSTRINGLIST, STRINGLIST ->
						throw new UnsupportedOperationException("Unimplemented case: " + setting.type);
					case ENUM -> {
						String enumName = config.getProperty(setting.ID, null);
						if (enumName != null) {
							Enum<?> enumValue = Enum.valueOf((((Enum<?>) setting.getValue()).getDeclaringClass()),
									enumName);
							setting.setValue(enumValue);
						}
					}
					case VEC3D -> {
						String[] components = value.split(",");
						if (components.length == 3) {
							float x = Float.parseFloat(components[0]);
							float y = Float.parseFloat(components[1]);
							float z = Float.parseFloat(components[2]);
							setting.setValue(new Vec3d(x, y, z));
						}
					}
					default -> throw new IllegalArgumentException("Unexpected value: " + setting.type);
					}
				} catch (Exception e) {
					LogUtils.getLogger().error(e.getMessage());
				}
			}
		} catch (Exception e) {
			LogUtils.getLogger().error(e.getMessage());
		}
	}
}
