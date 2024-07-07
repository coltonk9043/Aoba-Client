package net.aoba.settings;

import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class SettingsContainer {
    public String configName;
    public File configFolder;
    public File configFile;
    public HashSet<Setting<?>> settingsList = new HashSet<>();

    public SettingsContainer(String configName) throws IOException {
        this.configName = configName;
        configFolder = new File(MinecraftClient.getInstance().runDirectory + File.separator + "aoba");
        configFile = new File(configFolder + File.separator + configName + ".xml");

        if (!configFolder.exists())
            configFolder.mkdirs();

        if (!configFile.exists())
            configFile.createNewFile();
    }
}
