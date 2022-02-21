package net.aoba;

import net.aoba.altmanager.AltManager;
import net.aoba.cmd.CommandManager;
import net.aoba.gui.HudManager;
import net.aoba.misc.RenderUtils;
import net.aoba.module.ModuleManager;
import net.aoba.settings.Settings;
import net.minecraft.client.util.math.MatrixStack;

public class AobaClient {
	
	public static final String NAME = "Aoba";
	public static final String VERSION = "1.18.1";
	public static final String PREFIX = ".aoba";

	public ModuleManager mm;
	public CommandManager cm;
	public Settings settings;
	public AltManager am;
	public RenderUtils renderUtils;
	public HudManager hm;
	private boolean ghostMode;
	
	public AobaClient() {
		System.out.println("[Aoba] Starting Client");
	}
	
	public void Init() {
		renderUtils = new RenderUtils();
		System.out.println("[Aoba] Reading Settings");
		settings = new Settings();
		System.out.println("[Aoba] Initializing Modules");
		mm = new ModuleManager();
		System.out.println("[Aoba] Initializing Commands");
		cm = new CommandManager();
		System.out.println("[Aoba] Initializing GUI");
		hm = new HudManager();
		System.out.println("[Aoba] Loading Alts");
		am = new AltManager();
		System.out.println("[Aoba] Aoba-chan initialized and ready to play!");
		
	}
	
	public void update() {
		mm.update();
		hm.update();
	}

	public void drawHUD(MatrixStack matrixStack, float partialTicks) {
		if (!ghostMode) {
			hm.draw(matrixStack, partialTicks);
		}
	}

	public void toggleGhostMode() {
		ghostMode = !ghostMode;
	}
	
	public boolean isGhosted() {
		return this.ghostMode;
	}
	
	public void endClient() {
		settings.saveSettings();
		am.saveAlts();
		System.out.println("[Aoba] Shutting down...");
	}
}
