package net.aoba.macros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import net.aoba.macros.actions.KeyClickMacroEvent;
import net.aoba.macros.actions.MacroEvent;
import net.aoba.macros.actions.MouseClickMacroEvent;
import net.aoba.macros.actions.MouseMoveMacroEvent;
import net.aoba.macros.actions.MouseScrollMacroEvent;
import net.minecraft.client.MinecraftClient;

public class Macro {
	private String name;
	private String filePath;
	private LinkedList<MacroEvent> events;

	public Macro(File file) {
		this.name = file.getName();
		this.filePath = file.getPath();
		this.events = new LinkedList<MacroEvent>();
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(filePath));

			String className = null;
			while ((className = in.readUTF()) != null) {
				// Read Macros
				// TODO: I HATTEE writing stuff out in plaintext, but I also hate files that
				// cant be modified externally.
				// I can't think of a way to make these macros something that can be easily
				// modified in Notepad without
				// making a giant file. If possible, any chance we can write into xml format???
				MacroEvent event = null;
				if (className.equals(KeyClickMacroEvent.class.getName())) {
					event = new KeyClickMacroEvent();
				} else if (className.equals(MouseClickMacroEvent.class.getName())) {
					event = new MouseClickMacroEvent();
				} else if (className.equals(MouseMoveMacroEvent.class.getName())) {
					event = new MouseMoveMacroEvent();
				} else if (className.equals(MouseScrollMacroEvent.class.getName())) {
					event = new MouseScrollMacroEvent();
				} else
					System.out.println("Could not find Macro type.");

				if (event != null) {
					event.read(in);
					events.add(event);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Read all of the data.
		}
	}

	public Macro(LinkedList<MacroEvent> events) {
		this.events = events;
	}

	public String getName() {
		return name;
	}

	public String getFilePath() {
		return filePath;
	}

	public LinkedList<MacroEvent> getEvents() {
		return events;
	}

	public void setName(String name) {
		this.name = name;
		MinecraftClient MC = MinecraftClient.getInstance();
		this.filePath = MC.runDirectory + File.separator + "aoba" + File.separator + "macros" + File.separator + name
				+ ".macro";
	}

	public void save() {
		try {
			File macroFile = new File(filePath);
			if (!macroFile.exists() && !macroFile.createNewFile()) {
				throw new IOException("Failed to create config file: " + macroFile.getAbsolutePath());
			}

			DataOutputStream out = new DataOutputStream(new FileOutputStream(macroFile));
			MacroEvent event = events.poll();
			while (event != null) {
				event.write(out);
				event = events.poll();
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
