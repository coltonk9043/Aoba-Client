package net.aoba.macros.actions;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.aoba.mixin.interfaces.IMouse;
import net.minecraft.client.Mouse;

public class KeyClickMacroEvent extends MacroEvent {

	private int button = 0;
	private int scancode = 0;
	private int action= 0;
	private int mods= 0;
	
	public KeyClickMacroEvent(long timestamp, int button, int scancode, int action, int mods) {
		super(timestamp);
		this.button = button;
		this.scancode = scancode;
		this.action = action;
		this.mods = mods;
	}

	@Override
	public void write(FileOutputStream fs) {

	}

	@Override
	public void read(FileInputStream fs) {

	}

	@Override
	public void execute() {
		MC.keyboard.onKey(MC.getWindow().getHandle(), button, scancode, action, mods);
	}
}
