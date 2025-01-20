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

public class KeyClickMacroEvent extends MacroEvent {

	private int button = 0;
	private int scancode = 0;
	private int action= 0;
	private int mods= 0;
	
	public KeyClickMacroEvent() {
		
	}
	
	public KeyClickMacroEvent(long timestamp, int button, int scancode, int action, int mods) {
		super(timestamp);
		this.button = button;
		this.scancode = scancode;
		this.action = action;
		this.mods = mods;
	}

	@Override
	public void write(DataOutputStream fs) throws IOException {
		super.write(fs);
		fs.writeInt(button);
		fs.writeInt(action);
		fs.writeInt(scancode);
		fs.writeInt(mods);
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		super.read(in);
		button = in.readInt();
		scancode = in.readInt();
		action = in.readInt();
		mods = in.readInt();
	}

	@Override
	public void execute() {
		MC.keyboard.onKey(MC.getWindow().getHandle(), button, scancode, action, mods);
	}
}
