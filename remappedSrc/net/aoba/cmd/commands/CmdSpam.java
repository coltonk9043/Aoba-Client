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

import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;

public class CmdSpam extends Command {

	public CmdSpam() {
		this.description = "Spams the chat with a certain message";
	}

	@Override
	public void command(String[] parameters) {
		if (parameters.length > 1) {
			String message = "";
			for(int msg = 1; msg < parameters.length; msg++) {
				message = message + parameters[msg] + " ";
			}
			for(int i = 0; i < Integer.parseInt(parameters[0]); i++) {
				//mc.player.sendChatMessage(message);
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Use format '.aoba spam [Times] [Message]'");
		}
	}
}
