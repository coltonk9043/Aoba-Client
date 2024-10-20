package net.aoba.macros.actions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import net.aoba.mixin.interfaces.IMouse;
import net.minecraft.client.Mouse;

public class MouseMoveMacroEvent extends MacroEvent {
	private double x = 0;
	private double y = 0;
	
	public MouseMoveMacroEvent(long timestamp, double x, double y) {
		super(timestamp);
		this.x = x;
		this.y = y;
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
			iMouse.executeonCursorPos(MC.getWindow().getHandle(), x, y);
		}
	}
}
