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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import net.aoba.module.Module;
import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;

public class CmdHelp extends Command {

	int indexesPerPage = 5;

	public CmdHelp() {
		super("help", "Shows the avaiable commands.", "[page, module]");
	}

	@Override
	public void runCommand(String[] parameters) {
		if (parameters.length <= 0) {
			ShowCommands(1);
		} else if (StringUtils.isNumeric(parameters[0])) {
			int page = Integer.parseInt(parameters[0]);
			ShowCommands(page);
		} else {
			Module module = Aoba.getInstance().moduleManager.getModuleByName(parameters[0]);
			if (module == null) {
				CommandManager.sendChatMessage("Could not find Module '" + parameters[0] + "'.");
			} else {
				CommandManager.sendChatMessage("------------ " + module.getName() + "Help ------------");
				CommandManager.sendChatMessage("Name: " + module.getName());
				CommandManager.sendChatMessage("Description: " + module.getDescription());
				CommandManager.sendChatMessage("Keybind: " + module.getBind().getTranslationKey());
			}
		}

	}

	private void ShowCommands(int page) {
		CommandManager.sendChatMessage("------------ Help [Page " + page + " of 5] ------------");
		CommandManager.sendChatMessage("Use .aoba help [n] to get page n of help.");

		// Fetch the commands and dislays their syntax on the screen.
		HashMap<String, Command> commands = Aoba.getInstance().commandManager.getCommands();
		Set<String> keySet = commands.keySet();
		ArrayList<String> listOfCommands = new ArrayList<String>(keySet);
		 
		for (int i = (page - 1) * indexesPerPage; i <= (page * indexesPerPage); i++) {
			if (i >= 0 && i < Aoba.getInstance().commandManager.getNumOfCommands()) {
				CommandManager.sendChatMessage(" .aoba " + listOfCommands.get(i));
			}
		}
	}

	@Override
	public String[] getAutocorrect(String previousParameter) {
		// TODO Auto-generated method stub
		CommandManager cm = Aoba.getInstance().commandManager;
		int numCmds = cm.getNumOfCommands();
		String[] commands = new String[numCmds];

		Set<String> cmds = Aoba.getInstance().commandManager.getCommands().keySet();
		int i = 0;
        for (String x : cmds)
        	commands[i++] = x;
		
		return commands;
	}

}
