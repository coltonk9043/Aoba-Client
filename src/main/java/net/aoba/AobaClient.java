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

import com.mojang.logging.LogUtils;

import net.aoba.managers.*;
import net.aoba.managers.altmanager.AltManager;
import net.aoba.api.IAddon;
import net.aoba.command.GlobalChat;
import net.aoba.gui.GuiManager;
import net.aoba.gui.font.FontManager;
import net.aoba.managers.macros.MacroManager;
import net.aoba.mixin.interfaces.IMinecraftClient;
import net.aoba.managers.proxymanager.ProxyManager;
import net.aoba.settings.friends.FriendsList;
import net.aoba.utils.discord.RPCManager;
import net.aoba.managers.rotation.RotationManager;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;

public class AobaClient {
	public static final String NAME = "Aoba";
	public static final String VERSION = "1.21.3";
	public static final String AOBA_VERSION = "1.4.4";

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
	public RPCManager rpcManager;
	public SettingManager settingManager;
	public FriendsList friendsList;
	public GlobalChat globalChat;
	public EventManager eventManager;
	public MacroManager macroManager;
	public InteractionManager interactionManager;

	public static List<IAddon> addons = new ArrayList<>();

	/**
	 * Initializes Aoba Client and creates sub-systems.
	 */
	public void Initialize() {
		// Gets instance of Minecraft
		MC = MinecraftClient.getInstance();
		IMC = (IMinecraftClient) MC;
	}

	/**
	 * Initializes systems and loads any assets.
	 */
	public void loadAssets() {
		LogUtils.getLogger().info("[Aoba] Starting Client");
		eventManager = new EventManager();

		// Register any addons.
		LogUtils.getLogger().info("[Aoba] Starting addon initialization");
		for (EntrypointContainer<IAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("aoba",
				IAddon.class)) {
			IAddon addon = entrypoint.getEntrypoint();

			try {
				LogUtils.getLogger().info("[Aoba] Initializing addon: " + addon.getName());
				addon.onInitialize();
				LogUtils.getLogger().info("[Aoba] Addon initialized: " + addon.getName());
			} catch (Throwable e) {
				LogUtils.getLogger().error("Error initializing addon: " + addon.getName(), e.getMessage());
			}

			addons.add(addon);
		}

		LogUtils.getLogger().info("[Aoba] Reading Settings");
		settingManager = new SettingManager();
		LogUtils.getLogger().info("[Aoba] Reading Friends List");
		friendsList = new FriendsList();
		LogUtils.getLogger().info("[Aoba] Initializing Rotation Manager");
		rotationManager = new RotationManager();
		LogUtils.getLogger().info("[Aoba] Initializing Modules");
		moduleManager = new ModuleManager(addons);
		LogUtils.getLogger().info("[Aoba] Initializing Commands");
		commandManager = new CommandManager(addons);
		LogUtils.getLogger().info("[Aoba] Initializing Font Manager");
		fontManager = new FontManager();
		fontManager.Initialize();
		LogUtils.getLogger().info("[Aoba] Initializing Combat Manager");
		combatManager = new CombatManager();
		LogUtils.getLogger().info("[Aoba] Initializing Macro Manager");
		macroManager = new MacroManager();
		LogUtils.getLogger().info("[Aoba] Initializing GUI");
		guiManager = new GuiManager();
		guiManager.Initialize();
		LogUtils.getLogger().info("[Aoba] Loading Alts");
		altManager = new AltManager();
		proxyManager = new ProxyManager();
		interactionManager = new InteractionManager();
		LogUtils.getLogger().info("[Aoba] Initializing Interaction Manager");

		LogUtils.getLogger().info("[Aoba] Aoba-chan initialized and ready to play!");

		SettingManager.loadGlobalSettings();
		SettingManager.loadSettings();
		globalChat = new GlobalChat();
		globalChat.StartListener();

		// GuiManager.borderColor.setMode(ColorMode.Rainbow);
		// GuiManager.foregroundColor.setMode(ColorMode.Random);
	}

	/**
	 * Called when the client is shutting down. Saves persistent data.
	 */
	public void endClient() {
		try {
			SettingManager.saveSettings();
			altManager.saveAlts();
			friendsList.save();
			macroManager.save();
			moduleManager.modules.forEach(s -> s.onDisable());
		} catch (Exception e) {
			LogUtils.getLogger().error(e.getMessage());
		}
		LogUtils.getLogger().info("[Aoba] Shutting down...");
	}
}
