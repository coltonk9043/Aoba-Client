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
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.ColorSetting.ColorMode;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.IntegerSetting;
import net.aoba.utils.types.Vector2;
import net.minecraft.block.Block;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

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
                        config.setProperty(setting.ID, ((Vector2) setting.getValue()).x + ","
                            + ((Vector2) setting.getValue()).y);
                    }
                    case RECTANGLE -> {
                        config.setProperty(setting.ID, ((Rectangle) setting.getValue()).getX() + ","
                            + ((Rectangle) setting.getValue()).getY() + ","
                            + ((Rectangle) setting.getValue()).getWidth() + ","
                            + ((Rectangle) setting.getValue()).getHeight());
                    }
                    case COLOR -> {
                    	ColorSetting cSetting = (ColorSetting)setting;
                        String s = cSetting.getMode().name() + "," + ((Color) setting.getValue()).getColorAsHex();
                        config.setProperty(setting.ID, s);
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

                        config.setProperty(setting.ID, result.toString());
                    }
                    case ENUM -> {
                        config.setProperty(setting.ID, ((Enum<?>) setting.getValue()).name());
                    }
                    case VEC3D -> {
                        Vec3d vec = (Vec3d) setting.getValue();
                        config.setProperty(setting.ID, vec.x + "," + vec.y + "," + vec.z);
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
                        	FloatSetting floatSetting = (FloatSetting)setting;
                        	floatSetting.setValue(Float.parseFloat(value));
                        }
                        case INTEGER -> {
                        	IntegerSetting intSetting = (IntegerSetting)setting;
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
                        case VECTOR2 -> {
                            String[] dimensions = value.split(",");
                            if (dimensions.length == 2) {
                                setting.setValue(new Vector2(Float.parseFloat(dimensions[0]), Float.parseFloat(dimensions[1])));
                            }
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
                        	ColorSetting cSetting = (ColorSetting)setting;
                        	if(splits.length == 2) {
                        		ColorMode enumValue = Enum.valueOf(((ColorSetting) setting).getMode().getDeclaringClass(), splits[0]);
                        		long hexValue = Long.parseLong(splits[1].replace("#", ""), 16);
                                int Alpha = (int) ((hexValue) >> 24) & 0xFF;
                                int R = (int) ((hexValue) >> 16) & 0xFF;
                                int G = (int) ((hexValue) >> 8) & 0xFF;
                                int B = (int) (hexValue) & 0xFF;
                                
                                cSetting.setMode(enumValue);
                                if(enumValue == ColorMode.Solid) {
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
                                Enum<?> enumValue = Enum.valueOf(((EnumSetting<?>) setting).getValue().getDeclaringClass(), enumName);
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

    public enum SettingCategories {
        CONFIG, MODULES, HIDDEN
    }
}
