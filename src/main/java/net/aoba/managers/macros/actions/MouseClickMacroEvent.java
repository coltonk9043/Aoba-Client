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

import net.aoba.mixin.interfaces.IMouse;
import net.minecraft.client.Mouse;

public class MouseClickMacroEvent extends MacroEvent {

	private int button = 0;
	private int action= 0;
	private int mods = 0;
	
	public MouseClickMacroEvent() {
		
	}
	
	public MouseClickMacroEvent(long timestamp, int button, int action, int mods) {
		super(timestamp);
		this.button = button;
		this.action = action;
		this.mods = mods;
	}

	@Override
	public void write(DataOutputStream fs) throws IOException {
		super.write(fs);
		fs.writeInt(button);
		fs.writeInt(action);
		fs.writeInt(mods);
	}

	@Override
	public void read(DataInputStream in)throws IOException {
		super.read(in);
		button = in.readInt();
		action = in.readInt();
		mods = in.readInt();
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
