/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.macros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.mojang.logging.LogUtils;

import net.aoba.managers.macros.actions.KeyClickMacroEvent;
import net.aoba.managers.macros.actions.MacroEvent;
import net.aoba.managers.macros.actions.MouseClickMacroEvent;
import net.aoba.managers.macros.actions.MouseMoveMacroEvent;
import net.aoba.managers.macros.actions.MouseScrollMacroEvent;
import net.minecraft.client.MinecraftClient;

/**
 * Represents the Manager responsible for maintaining, creating, saving, and
 * loading Macros.
 */
public class MacroManager {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private final HashMap<Class<?>, String> MACRO_CLASS_TO_NAME = new HashMap<Class<?>, String>();
	private final HashMap<String, Class<?>> MACRO_NAME_TO_CLASS = new HashMap<String, Class<?>>();

	private final HashMap<String, Macro> macros = new HashMap<String, Macro>();

	private Macro currentSelected = null;

	private final MacroRecorder recorder;
	private final MacroPlayer player;

	public MacroManager() {
		// Register default macro types (I don't like this but reduces file size
		// significantly... redo later)
		register("key", KeyClickMacroEvent.class);
		register("click", MouseClickMacroEvent.class);
		register("move", MouseMoveMacroEvent.class);
		register("scroll", MouseScrollMacroEvent.class);

		recorder = new MacroRecorder();
		player = new MacroPlayer();
		load();
	}

	/**
	 * Registers a type of Macro (with a key) to the MacroManager.
	 * 
	 * @param name Key used to fetch the Macro class type.
	 * @param type Type to associate with the key.
	 */
	public void register(String name, Class<?> type) {
		MACRO_NAME_TO_CLASS.put(name, type);
		MACRO_CLASS_TO_NAME.put(type, name);
	}

	/**
	 * Loads all of the macros found in the runDirectory/aoba/macros
	 */
	private void load() {
		File macroDirectory = new File(MC.runDirectory + File.separator + "aoba" + File.separator + "macros");
		if (macroDirectory.exists() && macroDirectory.isDirectory()) {
			LogUtils.getLogger().info("Found Macro Directory: " + macroDirectory.getAbsolutePath());
			File[] files = macroDirectory.listFiles((dir, name) -> name.endsWith(".macro"));
			if (files != null) {
				for (File file : files) {
					try {
						Macro macro = loadMacroFromFile(file);
						macros.put(macro.getName(), macro);
					} catch (Exception e) {

					}
				}
			}
		}
	}

	/**
	 * Loads a Macro from a file.
	 * 
	 * @param file File to read from.
	 * @return Loaded Macro from the file.
	 */
	private Macro loadMacroFromFile(File file) {
		// Get information about Macro including name and file path.
		String name = file.getName();
		name = name.substring(0, name.length() - 6);
		String filePath = file.getPath();
		LinkedList<MacroEvent> events = new LinkedList<MacroEvent>();

		try {
			DataInputStream in = new DataInputStream(new FileInputStream(filePath));

			// Read until it cannot be read anymore.
			String className = null;
			while ((className = in.readUTF()) != null) {
				// Read Macros
				MacroEvent event;
				if (MACRO_NAME_TO_CLASS.containsKey(className)) {
					try {
						// Instantiate the class from the type found in the 'MACRO_NAME_TO_CLASS'
						// HashMap.
						Class<?> macroClass = MACRO_NAME_TO_CLASS.get(className);
						event = (MacroEvent) macroClass.getDeclaredConstructor().newInstance((Object[]) null);

						// If the Macro exists, read its content.
						if (event != null) {
							event.read(in);
							events.add(event);
						}
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						break;
					}
				}
			}
			in.close();
		} catch (EOFException eof) {
		} // Do nothing... file is OEF
		catch (IOException e1) {
			e1.printStackTrace();
		}

		// Create and add macros.
		Macro newMacro = new Macro(events);
		newMacro.setName(name);
		return newMacro;
	}

	public void save() {
		try {
			// Try and find the Macro folder. If none exists, then throw an exception.
			File macrosFolder = new File(MC.runDirectory + File.separator + "aoba" + File.separator + "macros");
			if (!macrosFolder.exists() && !macrosFolder.mkdirs()) {
				throw new IOException("Failed to create macro folder: " + macrosFolder.getAbsolutePath());
			}

			// Save each macro.
			for (Macro macro : macros.values()) {
				// Attempt to create the Macro File. Throws an exception if it fails.
				File macroFile = new File(macro.getFilePath());
				if (!macroFile.exists() && !macroFile.createNewFile()) {
					throw new IOException("Failed to create config file: " + macroFile.getAbsolutePath());
				}

				// Read all of the events.
				LinkedList<MacroEvent> events = macro.getEvents();
				DataOutputStream out = new DataOutputStream(new FileOutputStream(macroFile));
				MacroEvent event = events.poll();
				while (event != null) {
					out.writeUTF(MACRO_CLASS_TO_NAME.get(event.getClass()));
					event.write(out);
					event = events.poll();
				}
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a Macro to the Macro Manager.
	 * 
	 * @param macro Macro to add.
	 */
	public void addMacro(Macro macro) {
		macros.put(macro.getName(), macro);
	}

	/**
	 * Removes a Macro from the Macro Manager.
	 * 
	 * @param macro Macro to remove.
	 */
	public void removeMacro(Macro macro) {
		macros.remove(macro.getName(), macro);
	}

	/**
	 * Returns the instance of the MacroRecorder.
	 * 
	 * @return MacroRecorder
	 */
	public MacroRecorder getRecorder() {
		return recorder;
	}

	/**
	 * Returns the instance of the MacroPlayer
	 * 
	 * @return MacroPlayer
	 */
	public MacroPlayer getPlayer() {
		return player;
	}

	/**
	 * Returns the currently selected Macro in the MacroManager. The selected Macro
	 * is the one that has been most recently recorded.
	 * 
	 * @return Currently 'selected' Macro
	 */
	public Macro getCurrentlySelected() {
		return currentSelected;
	}

	/**
	 * Sets the currently selected Macro.
	 * 
	 * @param macro
	 */
	public void setCurrentlySelected(Macro macro) {
		currentSelected = macro;
	}

	/**
	 * Returns a list of all of the Macros
	 * 
	 * @return List of all Macros.
	 */
	public List<Macro> getMacros() {
		return new ArrayList<Macro>(macros.values());
	}
}
