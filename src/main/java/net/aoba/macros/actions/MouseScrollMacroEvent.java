package net.aoba.macros.actions;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.aoba.mixin.interfaces.IMouse;
import net.minecraft.client.Mouse;

public class MouseScrollMacroEvent extends MacroEvent {

	private double deltaX = 0;
	private double deltaY = 0;
	
	public MouseScrollMacroEvent(long timestamp, double deltaX, double deltaY) {
		super(timestamp);
		this.deltaX = deltaX;
		this.deltaY = deltaY;
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
			iMouse.executeOnMouseScroll(timestamp, deltaX, deltaY);
		}
	}
}
