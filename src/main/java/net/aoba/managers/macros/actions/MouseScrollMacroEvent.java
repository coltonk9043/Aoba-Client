/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers.macros.actions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.aoba.mixin.interfaces.IMouseHandler;
import net.minecraft.client.MouseHandler;

public class MouseScrollMacroEvent extends MacroEvent {

	private double deltaX = 0;
	private double deltaY = 0;
	
	public MouseScrollMacroEvent() {
		
	}
	
	public MouseScrollMacroEvent(long timestamp, double deltaX, double deltaY) {
		super(timestamp);
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	@Override
	public void write(DataOutputStream fs) throws IOException {
		super.write(fs);
		fs.writeDouble(deltaX);
		fs.writeDouble(deltaY);
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		super.read(in);
		deltaX = in.readDouble();
		deltaY = in.readDouble();
	}

	@Override
	public void execute() {
		MouseHandler mouse = MC.mouseHandler;
		IMouseHandler iMouse = (IMouseHandler)mouse;
		if(iMouse != null) {
			iMouse.executeOnMouseScroll(timestamp, deltaX, deltaY);
		}
	}
}
