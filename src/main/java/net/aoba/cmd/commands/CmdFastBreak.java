/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.misc.FastBreak;

public class CmdFastBreak extends Command {

	public CmdFastBreak() {
		super("fastbreak", "Decreases the time it takes to break blocks");
	}

	@Override
	public void runCommand(String[] parameters) {
		FastBreak module = (FastBreak) Aoba.getInstance().moduleManager.fastbreak;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "multiplier":
				try {
					float multiplier = Float.parseFloat(parameters[1]);
					module.setMultiplier(multiplier);
					module.toggle();
					CommandManager.sendChatMessage("FastBreak multiplier set to " + multiplier + "x");
					module.toggle();
				} catch (Exception e) {
					CommandManager.sendChatMessage("Invalid value.");
				}
				break;
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("FastBreak toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("FastBreak toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba fastbreak [multiplier, toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba fastbreak [multiplier, toggle] [value]");
		}
	}

	@Override
	public String[] getAutocorrect(String previousParameter) {
		// TODO Auto-generated method stub
		return null;
	}
}
