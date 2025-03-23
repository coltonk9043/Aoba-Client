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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.aoba.api.IAddon;
import net.aoba.command.GlobalChat;
import net.aoba.gui.GuiManager;
import net.aoba.gui.font.FontManager;
import net.aoba.managers.CombatManager;
import net.aoba.managers.CommandManager;
import net.aoba.managers.EntityManager;
import net.aoba.managers.EventManager;
import net.aoba.managers.ModuleManager;
import net.aoba.managers.SettingManager;
import net.aoba.managers.altmanager.AltManager;
import net.aoba.managers.macros.MacroManager;
import net.aoba.managers.proxymanager.ProxyManager;
import net.aoba.managers.rotation.RotationManager;
import net.aoba.mixin.interfaces.IMinecraftClient;
import net.aoba.module.Module;
import net.aoba.settings.friends.FriendsList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;

public class AobaClient {
	public static final String AOBA_VERSION = "1.4.5";

	public static MinecraftClient MC;
	public static IMinecraftClient IMC;

	// Systems
	public RotationManager rotationManager;
	public ModuleManager moduleManager;
	public CommandManager commandManager;
	public AltManager altManager;
	public ProxyManager proxyManager;
	public GuiManager guiManager;
	public FontManager fontManager;
	public CombatManager combatManager;
	public SettingManager settingManager;
	public FriendsList friendsList;
	public GlobalChat globalChat;
	public EventManager eventManager;
	public MacroManager macroManager;
	public EntityManager entityManager;

	public static List<IAddon> addons = new ArrayList<>();
	public static Logger LOGGER;

	/**
	 * Initializes Aoba Client and creates sub-systems.
	 */
	public void Initialize() {
		// Gets instance of Minecraft
		MC = MinecraftClient.getInstance();
		IMC = (IMinecraftClient) MC;
		LOGGER = LogUtils.getLogger();
	}

	/**
	 * Initializes systems and loads any assets.
	 */
	public void loadAssets() {
		LOGGER.info("[Aoba] Starting Client");
		eventManager = new EventManager();

		// Register any addons.
		LogUtils.getLogger().info("[Aoba] Starting addon initialization");
		for (EntrypointContainer<IAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("aoba",
				IAddon.class)) {
			IAddon addon = entrypoint.getEntrypoint();

			try {
				LOGGER.info("[Aoba] Initializing addon: " + addon.getName());
				addon.onInitialize();
				LOGGER.info("[Aoba] Addon initialized: " + addon.getName());
			} catch (Throwable e) {
				LOGGER.error("Error initializing addon: " + addon.getName(), e.getMessage());
			}

			addons.add(addon);
		}

		LOGGER.info("[Aoba] Reading Settings");
		settingManager = new SettingManager();

		LOGGER.info("[Aoba] Reading Friends List");
		friendsList = new FriendsList();

		LOGGER.info("[Aoba] Initializing Rotation Manager");
		rotationManager = new RotationManager();

		LOGGER.info("[Aoba] Initializing Modules");
		moduleManager = new ModuleManager(addons);

		LOGGER.info("[Aoba] Initializing Commands");
		commandManager = new CommandManager(addons);

		LOGGER.info("[Aoba] Initializing Font Manager");
		fontManager = new FontManager();
		fontManager.Initialize();

		LOGGER.info("[Aoba] Initializing Combat Manager");
		combatManager = new CombatManager();

		LOGGER.info("[Aoba] Initializing Entity Manager");
		entityManager = new EntityManager();

		LOGGER.info("[Aoba] Initializing Macro Manager");
		macroManager = new MacroManager();

		LOGGER.info("[Aoba] Initializing GUI");
		guiManager = new GuiManager();
		guiManager.Initialize();

		LOGGER.info("[Aoba] Initializing Alt Manager");
		altManager = new AltManager();

		LOGGER.info("[Aoba] Initializing Proxy Manager");
		proxyManager = new ProxyManager();

		LOGGER.info("[Aoba] Registering Shutdown Hook");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				endClient();
			} catch (Exception e) {
				LOGGER.error("[Aoba] Error during shutdown: ", e);
			}
		}));

		LOGGER.info("[Aoba] Loading Settings");
		SettingManager.loadGlobalSettings();
		SettingManager.loadSettings();

		LOGGER.info("[Aoba] Initializing Global Chat");
		globalChat = new GlobalChat();
		globalChat.StartListener();

		LOGGER.info("[Aoba] Aoba-chan initialized and ready to play!");

		// GuiManager.borderColor.setMode(ColorMode.Rainbow);
		// GuiManager.foregroundColor.setMode(ColorMode.Random);
	}

	/**
	 * Called when the client is shutting down. Saves persistent data.
	 */
	public void endClient() {
		LOGGER.info("[Aoba] Shutting down");
		try {
			SettingManager.saveSettings();
			altManager.saveAlts();
			friendsList.save();
			macroManager.save();
			moduleManager.modules.forEach(Module::onDisable);
		} catch (Exception e) {
			LOGGER.error("[Aoba] Error saving data", e);
		}
	}
}
