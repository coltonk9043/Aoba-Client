package net.aoba.macros.actions;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.aoba.mixin.interfaces.IMouse;
import net.minecraft.client.Mouse;

public class MouseClickMacroEvent extends MacroEvent {

	private int button = 0;
	private int action= 0;
	private int mods = 0;
	
	public MouseClickMacroEvent(long timestamp, int button, int action, int mods) {
		super(timestamp);
		this.button = button;
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
		Mouse mouse = MC.mouse;
		IMouse iMouse = (IMouse)mouse;
		if(iMouse != null) {
			iMouse.executeOnMouseButton(MC.getWindow().getHandle(), button, action, mods);
		}
	}
}
