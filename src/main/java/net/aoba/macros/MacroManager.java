package net.aoba.macros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.MinecraftClient;

public class MacroManager {
	private static MinecraftClient MC = MinecraftClient.getInstance();
	private HashMap<String, Macro> macros = new HashMap<String, Macro>();

	private Macro currentSelected = null;

	private MacroRecorder recorder;
	private MacroPlayer player;

	public MacroManager() {
		recorder = new MacroRecorder();
		player = new MacroPlayer();
		load();
	}

	private void load() {
		/*
		 * File macroDirectory = new File(MC.runDirectory + File.separator + "aoba" +
		 * File.separator + "macros");
		 * 
		 * if (macroDirectory.exists() && macroDirectory.isDirectory()) {
		 * LogUtils.getLogger().info("Found Macro Directory: " +
		 * macroDirectory.getAbsolutePath()); File[] files =
		 * macroDirectory.listFiles((dir, name) -> name.endsWith(".macro"));
		 * 
		 * if (files != null) { for (File file : files) { try { Macro macro = new
		 * Macro(file); macros.put(macro.getName(), macro); } catch (Exception e) {
		 * 
		 * } } } }
		 */
	}

	public void save() {
		/*
		 * try { File macrosFolder = new File(
		 * MinecraftClient.getInstance().runDirectory + File.separator + "aoba" +
		 * File.separator + "macros"); if (!macrosFolder.exists() &&
		 * !macrosFolder.mkdirs()) { throw new
		 * IOException("Failed to create macro folder: " +
		 * macrosFolder.getAbsolutePath()); }
		 * 
		 * for (Macro macro : macros.values()) { macro.save(); } } catch (Exception e) {
		 * 
		 * }
		 */
	}

	public void addMacro(Macro macro) {
		macros.put(macro.getName(), macro);
	}

	public MacroRecorder getRecorder() {
		return recorder;
	}

	public MacroPlayer getPlayer() {
		return player;
	}

	public Macro getCurrentlySelected() {
		return currentSelected;
	}

	public void setCurrentlySelected(Macro macro) {
		this.currentSelected = macro;
	}

	public List<Macro> getMacros() {
		return new ArrayList<Macro>(macros.values());
	}
}
