package aoba.main;

import aoba.main.altmanager.AltManager;
import aoba.main.cmd.CommandManager;
import aoba.main.gui.HudManager;
import aoba.main.gui.OldHud;
import aoba.main.misc.Settings;
import aoba.main.module.ModuleManager;
import net.minecraft.client.Minecraft;

public class Aoba {
	public static final String NAME = "Aoba";
	public static final String VERSION = "1.16.3";
	public static final String PREFIX = ".aoba";
	public ModuleManager mm;
	public CommandManager cm;
	public Settings settings;
	public AltManager am;
	public OldHud hud;
	public HudManager hm;
	private boolean ghostMode;

	public void startClient() {
		Minecraft.LOGGER.info("[Aoba] Starting Client");
		Minecraft.LOGGER.info("[Aoba] Initializing Modules");
		mm = new ModuleManager();
		Minecraft.LOGGER.info("[Aoba] Initializing Commands");
		cm = new CommandManager();
		Minecraft.LOGGER.info("[Aoba] Initializing GUI");
		hud = new OldHud();
		hm = new HudManager();
		Minecraft.LOGGER.info("[Aoba] Reading Settings");
		settings = new Settings();
		Minecraft.LOGGER.info("[Aoba] Loading Alts");
		am = new AltManager();
		Minecraft.LOGGER.info("[Aoba] Aoba-chan initialized and ready to play!");
	}

	public void update() {
		mm.update();
		hm.update();
	}

	public void drawHUD(int scaledWidth, int scaledHeight) {
		hud.update();
		if (!ghostMode) {
			hm.draw();
			hud.draw(scaledWidth, scaledHeight);
		}
	}

	public void toggleGhostMode() {
		ghostMode = !ghostMode;
	}
	
	public void endClient() {
		settings.saveSettings();
		am.saveAlts();
		Minecraft.LOGGER.info("[Aoba] Shutting down...");
	}
}
