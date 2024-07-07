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

package net.aoba.settings;

import com.mojang.logging.LogUtils;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.IntegerSetting;
import net.aoba.utils.types.Vector2;
import net.minecraft.block.Block;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class SettingManager {
    private static boolean DEBUG_STUFF = false;

    public SettingsContainer configContainer;
    public SettingsContainer modulesContainer;
    public SettingsContainer hiddenContainer;

    public SettingManager() {
        try {
            configContainer = new SettingsContainer("config_category");
            modulesContainer = new SettingsContainer("modules_category");
            hiddenContainer = new SettingsContainer("hidden_category");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registerSetting(Setting<?> p_setting, SettingsContainer p_category) {
        p_category.settingsList.add(p_setting);
    }

    public static Properties prepare(SettingsContainer container) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(container.configFile)) {
            props.loadFromXML(fis);
        } catch (InvalidPropertiesFormatException e) {
            LogUtils.getLogger().error("Invalid XML format in properties file: " + e.getMessage());
        } catch (IOException e) {
            LogUtils.getLogger().error("IOException while loading properties file: " + e.getMessage());
        }
        return props;
    }

    public static void saveSettings(SettingsContainer container)
            throws FileNotFoundException, IOException {
        LogUtils.getLogger().info("Saving config " + container.configName + ".");
        Properties config = prepare(container);
        for (Setting<?> setting : container.settingsList) {
            try {
                switch (setting.type) {
                    case FLOAT, INTEGER, BOOLEAN, STRING -> {
                        config.setProperty(setting.ID, String.valueOf(setting.getValue()));
                    }
                    case KEYBIND -> {
                        Key key = ((Key) setting.getValue());
                        config.setProperty(setting.ID, String.valueOf(key.getCode()));
                    }
                    case VECTOR2 -> {
                        config.setProperty(setting.ID, String.valueOf(((Vector2) setting.getValue()).x) + ","
                                + String.valueOf(((Vector2) setting.getValue()).y));
                    }
                    case COLOR -> {
                        String s = ((Color) setting.getValue()).getColorAsHex();
                        config.setProperty(setting.ID, s);
                    }
                    case BLOCKS -> {
                        @SuppressWarnings("unchecked")
                        HashSet<Block> s = (HashSet<Block>) setting.getValue();
                        String result = "";

                        int iteration = 0;
                        for (Block block : s) {
                            Identifier id = Registries.BLOCK.getId(block);
                            result += id.getNamespace() + ":" + id.getPath();
                            if (iteration != s.size() - 1) {
                                result += ",";
                            }
                            iteration++;
                        }

                        config.setProperty(setting.ID, result);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        config.storeToXML(new FileOutputStream(container.configFile), null);
    }

    public static void loadSettings(SettingsContainer container) {
        try {
            LogUtils.getLogger().info("Loading config " + container.configName + ".");
            Properties config = prepare(container);

            for (Setting setting : container.settingsList) {
                try {
                    String value = config.getProperty(setting.ID, null);

                    if (DEBUG_STUFF)
                        LogUtils.getLogger().info(setting.displayName + " " + setting.value + " " + Double.parseDouble(value));

                    if (value == null)
                        break;

                    switch (setting.type) {
                        case FLOAT -> {
                            float floatValue = Float.parseFloat(value);
                            if (((FloatSetting) setting).min_value <= floatValue
                                    && ((FloatSetting) setting).max_value >= floatValue) {
                                setting.setValue(Float.parseFloat(value));
                            }
                            break;
                        }
                        case INTEGER -> {
                            int intValue = Integer.parseInt(value);
                            if (((IntegerSetting) setting).min_value <= intValue
                                    && ((IntegerSetting) setting).max_value >= intValue) {
                                setting.setValue(Integer.parseInt(value));
                            }
                            break;
                        }
                        case BOOLEAN -> {
                            setting.setValue(Boolean.parseBoolean(value));
                            break;
                        }
                        case STRING -> {
                            setting.setValue(value);
                            break;
                        }
                        case KEYBIND -> {
                            int keyCode = Integer.parseInt(config.getProperty(setting.ID, null));
                            setting.setValue(InputUtil.fromKeyCode(keyCode, 0));
                            break;
                        }
                        case VECTOR2 -> {
                            String[] dimensions = value.split(",");
                            if (dimensions.length == 2) {
                                setting.setValue(new Vector2(Float.parseFloat(dimensions[0]), Float.parseFloat(dimensions[1])));
                            }
                            break;
                        }
                        case COLOR -> {
                            long hexValue = Long.parseLong(value.replace("#", ""), 16);
                            int Alpha = (int) ((hexValue) >> 24) & 0xFF;
                            int R = (int) ((hexValue) >> 16) & 0xFF;
                            int G = (int) ((hexValue) >> 8) & 0xFF;
                            int B = (int) (hexValue) & 0xFF;
                            setting.setValue(new Color(R, G, B, Alpha));
                            break;
                        }
                        case BLOCKS -> {
                            String[] ids = value.split(",");
                            HashSet<Block> result = new HashSet<Block>();
                            for (String str : ids) {
                                Identifier i = Identifier.of(str);
                                result.add(Registries.BLOCK.get(i));
                            }
                            setting.setValue(result);
                            break;
                        }
                        case INDEXEDSTRINGLIST ->
                                throw new UnsupportedOperationException("Unimplemented case: " + setting.type);
                        case STRINGLIST ->
                                throw new UnsupportedOperationException("Unimplemented case: " + setting.type);
                        default -> throw new IllegalArgumentException("Unexpected value: " + setting.type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
