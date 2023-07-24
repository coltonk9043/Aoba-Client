package net.aoba.core.osettings;

import net.aoba.core.osettings.OSetting;
import net.aoba.core.osettings.osettingtypes.DoubleOSetting;
import net.aoba.core.osettings.osettingtypes.IntegerOSetting;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OSettingManager {
    private static boolean DEBUG_STUFF = false;

    public List<OSetting> config_category = new ArrayList<>();
    public List<OSetting> modules_category = new ArrayList<>();
    public List<OSetting> hidden_category = new ArrayList<>();

    public static void register_setting (OSetting p_setting, List<OSetting> p_category) {
        p_category.add(p_setting);
    }

    public static OSetting get_setting_in_category (String p_setting_id, List<OSetting> p_category) {
        for (OSetting setting : p_category) {
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
            configFolder = new File(MinecraftClient.getInstance().runDirectory + File.separator + "Aoba");
            configFile = new File(configFolder + File.separator + name + ".xml");
            if (!configFolder.exists()) configFolder.mkdirs();
            if (!configFile.exists()) configFile.createNewFile();
            config = new Properties();
        } catch (Exception ignored) {}
    }

    public static void saveSettings(String name, List<OSetting> setting_list) {
        try {
            System.out.println("Saving config " + name + ".");
            prepare(name);
            for (OSetting setting : setting_list) {
                config.setProperty(setting.ID, String.valueOf(setting.getValue()));
            }
            config.storeToXML(new FileOutputStream(configFile), null);
        } catch (Exception ignored) {}
    }

    public static void loadSettings(String name, List<OSetting> setting_list) {
        try {
            System.out.println("Loading config " + name + ".");
            prepare(name);
            config.loadFromXML(new FileInputStream(configFile));
            for (OSetting setting : setting_list) {
                String value = config.getProperty(setting.ID, null);
                if (value != null) {
                    switch (setting.type) {
                        case DOUBLE -> {
                            if (((DoubleOSetting) setting).min_value <= Double.parseDouble(value) && ((DoubleOSetting) setting).max_value >= Double.parseDouble(value)) {
                                if (DEBUG_STUFF) System.out.println(setting.name + " " + setting.value + " " + Double.parseDouble(value));
                                setting.setValue(Double.parseDouble(value));
                            }
                        }
                        case INTEGER -> {
                            if ((int) ((IntegerOSetting) setting).min_value <= Integer.parseInt(value) && (int) ((IntegerOSetting) setting).max_value >= Integer.parseInt(value)) {
                                if (DEBUG_STUFF) System.out.println(setting.name + " " + setting.value + " " + Integer.parseInt(value));
                                setting.setValue(Integer.parseInt(value));
                            }
                        }
                        case BOOLEAN -> {
                            if (DEBUG_STUFF) System.out.println(setting.name + " " + setting.value + " " + Boolean.parseBoolean(value));
                            setting.setValue(Boolean.parseBoolean(value));
                        }
                        case STRING -> {
                            if (DEBUG_STUFF) System.out.println(setting.name + " " + setting.value + " " + value);
                            setting.setValue(value);
                        }
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public enum SettingCategories {
        CONFGI,
        MODULES,
        HIDDEN
    }
}
