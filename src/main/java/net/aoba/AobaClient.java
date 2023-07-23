/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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

import net.aoba.altmanager.AltManager;
import net.aoba.cmd.CommandManager;
import net.aoba.gui.HudManager;
import net.aoba.interfaces.IMinecraftClient;
import net.aoba.misc.RenderUtils;
import net.aoba.module.ModuleManager;
import net.aoba.settings.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class AobaClient {
	public static final String NAME = "Aoba";
	public static final String VERSION = "1.20.1";
	public static final String PREFIX = ".aoba";

	public static MinecraftClient MC;
	public static IMinecraftClient IMC;
	
	// Systems
	public ModuleManager moduleManager;
	public CommandManager commandManager;
	public AltManager altManager;
	public HudManager hudManager;
	public Settings settings;
	public RenderUtils renderUtils;
	
	private boolean ghostMode;
	
	/**
	 * Initializes Aoba Client and creates sub-systems.
	 */
	public void Initialize() {
		// Gets instance of Minecraft
		MC = MinecraftClient.getInstance();
		IMC = (IMinecraftClient)MC;
		
		System.out.println("[Aoba] Starting Client");
		renderUtils = new RenderUtils();
		System.out.println("[Aoba] Reading Settings");
		settings = new Settings();
		System.out.println("[Aoba] Initializing Modules");
		moduleManager = new ModuleManager();
		System.out.println("[Aoba] Initializing Commands");
		commandManager = new CommandManager();
		System.out.println("[Aoba] Initializing GUI");
		hudManager = new HudManager();
		System.out.println("[Aoba] Loading Alts");
		altManager = new AltManager();
		System.out.println("[Aoba] Aoba-chan initialized and ready to play!");
	}
	
	/**
	 * Updates Aoba on a per-tick basis.
	 */
	public void update() {
		moduleManager.update();
		hudManager.update();
	}

	/**
	 * Renders the HUD every frame
	 * @param context The current Matrix Stack
	 * @param partialTicks Delta between ticks
	 */
	public void drawHUD(DrawContext context, float partialTicks) {
		// If the program is not in Ghost Mode, draw UI.
		if (!ghostMode) {
			hudManager.draw(context, partialTicks);
		}
	}

	/**
	 * Toggles Ghost Mode. (No UI)
	 */
	public void toggleGhostMode() {
		ghostMode = !ghostMode;
	}
	
	/**
	 * Returns whether Aoba is currently in Ghost Mode. (No UI)
	 * @return Ghost Mode
	 */
	public boolean isGhosted() {
		return this.ghostMode;
	}
	
	/**
	 * Called when the client is shutting down.
	 */
	public void endClient() {
		settings.saveSettings();
		altManager.saveAlts();
		System.out.println("[Aoba] Shutting down...");
	}
}
