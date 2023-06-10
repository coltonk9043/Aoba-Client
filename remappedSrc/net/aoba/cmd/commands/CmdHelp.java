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

import org.apache.commons.lang3.StringUtils;
import net.aoba.module.Module;
import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;

public class CmdHelp extends Command {

	int indexesPerPage = 5;

	public CmdHelp() {
		this.description = "Shows the avaiable commands.";
	}

	@Override
	public void command(String[] parameters) {
		if (StringUtils.isNumeric(parameters[0])) {
			CommandManager.sendChatMessage("------------ Help [Page " + parameters[0] + " of 4] ------------");
			CommandManager.sendChatMessage("Use .aoba help [n] to get page n of help.");
			
			// Fetch the commands.
			String[] commands = (String[])Aoba.getInstance().cm.getCommands().values().toArray();
			for (int i = (Integer.parseInt(parameters[0]) - 1)
					* indexesPerPage; i <= (Integer.parseInt(parameters[0]) * indexesPerPage
							+ indexesPerPage); i++) {
				try {
					if (!(i > Aoba.getInstance().cm.getNumOfCommands())) {
						CommandManager.sendChatMessage(" .aoba " + commands[i]);
					}
				}catch(Exception e) {
					
				}
			}
		} else {
			Module module = Aoba.getInstance().mm.getModuleByName(parameters[0]);
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

}
