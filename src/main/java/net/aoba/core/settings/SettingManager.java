package net.aoba.core.settings;

import net.aoba.core.settings.types.FloatSetting;
import net.aoba.core.settings.types.IntegerSetting;
import net.aoba.core.utils.types.Vector2;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SettingManager {
    private static boolean DEBUG_STUFF = false;

    public List<Setting> config_category = new ArrayList<>();
    public List<Setting> modules_category = new ArrayList<>();
    public List<Setting> hidden_category = new ArrayList<>();

    public static void register_setting (Setting p_setting, List<Setting> p_category) {
        p_category.add(p_setting);
    }

    public static Setting get_setting_in_category (String p_setting_id, List<Setting> p_category) {
        for (Setting setting : p_category) {
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
            if (!configFolder.exists()) configFolder.mkdirs();
            if (!configFile.exists()) configFile.createNewFile();
            config = new Properties();
        } catch (Exception ignored) {}
    }

    public static void saveSettings(String name, List<Setting> setting_list) {
        try {
            System.out.println("Saving config " + name + ".");
            prepare(name);
            for (Setting setting : setting_list) {
                switch (setting.type) {
                    case DOUBLE, INTEGER, BOOLEAN, STRING -> {
                        config.setProperty(setting.ID, String.valueOf(setting.getValue()));
                    }
                    case VECTOR2 -> {
                        config.setProperty(setting.ID + "_x", String.valueOf(((Vector2)setting.getValue()).x));
                        config.setProperty(setting.ID + "_y", String.valueOf(((Vector2)setting.getValue()).y));
                    }
                }
                // config.setProperty(setting.ID, String.valueOf(setting.getValue()));
            }
            config.storeToXML(new FileOutputStream(configFile), null);
        } catch (Exception ignored) {}
    }

    public static void loadSettings(String name, List<Setting> setting_list) {
        try {
            System.out.println("Loading config " + name + ".");
            prepare(name);
            config.loadFromXML(new FileInputStream(configFile));
            for (Setting setting : setting_list) {
                switch (setting.type) {
                    case DOUBLE, INTEGER, BOOLEAN, STRING -> {
                        String value = config.getProperty(setting.ID, null);
                        if (value == null) break;
                        switch (setting.type) {
                            case DOUBLE -> {
                                if (((FloatSetting) setting).min_value <= Double.parseDouble(value) && ((FloatSetting) setting).max_value >= Double.parseDouble(value)) {
                                    if (DEBUG_STUFF) System.out.println(setting.displayName + " " + setting.value + " " + Double.parseDouble(value));
                                    setting.setValue(Double.parseDouble(value));
                                }
                            }
                            case INTEGER -> {
                                if ((int) ((IntegerSetting) setting).min_value <= Integer.parseInt(value) && (int) ((IntegerSetting) setting).max_value >= Integer.parseInt(value)) {
                                    if (DEBUG_STUFF) System.out.println(setting.displayName + " " + setting.value + " " + Integer.parseInt(value));
                                    setting.setValue(Integer.parseInt(value));
                                }
                            }
                            case BOOLEAN -> {
                                if (DEBUG_STUFF) System.out.println(setting.displayName + " " + setting.value + " " + Boolean.parseBoolean(value));
                                setting.setValue(Boolean.parseBoolean(value));
                            }
                            case STRING -> {
                                if (DEBUG_STUFF) System.out.println(setting.displayName + " " + setting.value + " " + value);
                                setting.setValue(value);
                            }
                        }
                    }

                    case VECTOR2 -> {
                        String value_x = config.getProperty(setting.ID + "_x", null);
                        String value_y = config.getProperty(setting.ID + "_y", null);
                        if (value_x == null || value_y == null) break;
                        setting.setValue(new Vector2(Float.parseFloat(value_x), Float.parseFloat(value_y)));
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public enum SettingCategories {
        CONFIG,
        MODULES,
        HIDDEN
    }
}
