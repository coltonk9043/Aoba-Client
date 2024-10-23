package net.aoba.settings;

import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class SettingsContainer {
    public String configName;
    public File configFolder;package net.aoba.settings;

import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SettingsContainer {
    private final String configName;
    private final File configFolder;
    private final File configFile;
    private final Set<Setting<?>> settingsList = new HashSet<>();

    public SettingsContainer(String configName) throws IOException {
        this.configName = configName;
        configFolder = new File(MinecraftClient.getInstance().runDirectory, "aoba");
        configFile = new File(configFolder, configName + ".xml");

        if (!configFolder.exists() && !configFolder.mkdirs()) {
            throw new IOException("Failed to create config folder: " + configFolder.getAbsolutePath());
        }

        if (!configFile.exists() && !configFile.createNewFile()) {
            throw new IOException("Failed to create config file: " + configFile.getAbsolutePath());
        }
    }

    public String getConfigName() {
        return configName;
    }

    public File getConfigFolder() {
        return configFolder;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Set<Setting<?>> getSettingsList() {
        return settingsList;
    }
}
    public File configFile;
    public HashSet<Setting<?>> settingsList = new HashSet<>();

    public SettingsContainer(String configName) throws IOException {
        this.configName = configName;
        configFolder = new File(MinecraftClient.getInstance().runDirectory + File.separator + "aoba");
        configFile = new File(configFolder + File.separator + configName + ".xml");

        if (!configFolder.exists() && !configFolder.mkdirs()) {
            throw new IOException("Failed to create config folder: " + configFolder.getAbsolutePath());
        }

        if (!configFile.exists() && !configFile.createNewFile()) {
            throw new IOException("Failed to create config file: " + configFile.getAbsolutePath());
        }
    }
}
