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

/**
 * A class to represent Aoba Client and all of its functions.
 */
package net.aoba;

import com.mojang.logging.LogUtils;
import net.aoba.altmanager.AltManager;
import net.aoba.api.IAddon;
import net.aoba.cmd.CommandManager;
import net.aoba.cmd.GlobalChat;
import net.aoba.event.EventManager;
import net.aoba.gui.GuiManager;
import net.aoba.gui.font.FontManager;
import net.aoba.misc.RenderUtils;
import net.aoba.mixin.interfaces.IMinecraftClient;
import net.aoba.module.ModuleManager;
import net.aoba.proxymanager.ProxyManager;
import net.aoba.settings.SettingManager;
import net.aoba.settings.friends.FriendsList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class AobaClient {
    public static final String NAME = "Aoba";
    public static final String VERSION = "1.21";
    public static final String AOBA_VERSION = "1.4.2";

    public static MinecraftClient MC;
    public static IMinecraftClient IMC;

    // Systems
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public AltManager altManager;
    public ProxyManager proxyManager;
    public GuiManager hudManager;
    public FontManager fontManager;
    public SettingManager settingManager;
    public FriendsList friendsList;
    public RenderUtils renderUtils;
    public GlobalChat globalChat;
    public EventManager eventManager;
    public static List<IAddon> addons = new ArrayList<>();

    /**
     * Initializes Aoba Client and creates sub-systems.
     */
    public void Initialize() {
        // Gets instance of Minecraft
        MC = MinecraftClient.getInstance();
        IMC = (IMinecraftClient) MC;
    }

    public void loadAssets() {
        LogUtils.getLogger().info("[Aoba] Starting Client");

        eventManager = new EventManager();

        LogUtils.getLogger().info("[Aoba] Starting addon initialization");

        for (EntrypointContainer<IAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("aoba", IAddon.class)) {
            IAddon addon = entrypoint.getEntrypoint();

            try {
                LogUtils.getLogger().info("[Aoba] Initializing addon: " + addon.getClass().getName());
                addon.onInitialize();
                LogUtils.getLogger().info("[Aoba] Addon initialized: " + addon.getClass().getName());
            } catch (Throwable e) {
                LogUtils.getLogger().error("Error initializing addon: " + addon.getClass().getName(), e);
            }

            addons.add(addon);
        }

        LogUtils.getLogger().info("[Aoba] Addon initialization completed");

        renderUtils = new RenderUtils();
        LogUtils.getLogger().info("[Aoba] Reading Settings");
        settingManager = new SettingManager();
        LogUtils.getLogger().info("[Aoba] Reading Friends List");
        friendsList = new FriendsList();
        LogUtils.getLogger().info("[Aoba] Initializing Modules");
        moduleManager = new ModuleManager(addons);
        LogUtils.getLogger().info("[Aoba] Initializing Commands");
        commandManager = new CommandManager(addons);
        LogUtils.getLogger().info("[Aoba] Initializing Font Manager");
        fontManager = new FontManager();
        fontManager.Initialize();
        LogUtils.getLogger().info("[Aoba] Initializing GUI");
        hudManager = new GuiManager();
        hudManager.Initialize();
        LogUtils.getLogger().info("[Aoba] Loading Alts");
        altManager = new AltManager();
        proxyManager = new ProxyManager();
        LogUtils.getLogger().info("[Aoba] Aoba-chan initialized and ready to play!");

        SettingManager.loadSettings(settingManager.configContainer);
        SettingManager.loadSettings(settingManager.modulesContainer);
        SettingManager.loadSettings(settingManager.hiddenContainer);

        globalChat = new GlobalChat();
        globalChat.StartListener();

        //GuiManager.borderColor.setMode(ColorMode.Rainbow);
        //GuiManager.foregroundColor.setMode(ColorMode.Random);
    }

    /**
     * Called when the client is shutting down.
     */
    public void endClient() {
        try {
            SettingManager.saveSettings(settingManager.configContainer);
            SettingManager.saveSettings(settingManager.modulesContainer);
            SettingManager.saveSettings(settingManager.hiddenContainer);
            altManager.saveAlts();
            friendsList.save();
            moduleManager.modules.forEach(s -> s.onDisable());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.getLogger().info("[Aoba] Shutting down...");
    }
}
