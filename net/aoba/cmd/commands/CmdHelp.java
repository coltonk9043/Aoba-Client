package net.aoba.cmd.commands;

import org.apache.commons.lang3.StringUtils;
import net.aoba.module.Module;
import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;

public class CmdHelp extends Command {

	int indexesPerPage = 5;

	public CmdHelp() {
		this.command = "help";
		this.description = "Shows the avaiable commands.";
	}

	@Override
	public void command(String[] parameters) {
		if (StringUtils.isNumeric(parameters[0])) {
			CommandManager.sendChatMessage("Â§5â•�â•�â•�â•�â•�â•�â•�â•� Help [Page " + parameters[0] + " of 4] â•�â•�â•�â•�â•�â•�â•�â•�");
			CommandManager.sendChatMessage("Use .aoba help [n] to get page n of help.");
			for (int i = (Integer.parseInt(parameters[0]) - 1)
					* indexesPerPage; i <= (Integer.parseInt(parameters[0]) * indexesPerPage
							+ indexesPerPage); i++) {
				try {
					if (!(i > Aoba.getInstance().cm.commands.size())) {
						CommandManager.sendChatMessage(" .aoba " + Aoba.getInstance().cm.commands.get(i).getCommand());
					}
				}catch(Exception e) {
					
				}
			}
		} else {
			Module module = Aoba.getInstance().mm.getModuleByName(parameters[0]);
			if (module == null) {
				CommandManager.sendChatMessage("Could not find Module '" + parameters[0] + "'.");
			} else {
				CommandManager.sendChatMessage("Â§5â•�â•�â•�â•�â•�â•�â•�â•�â•� " + module.getName() + "Help â•�â•�â•�â•�â•�â•�â•�â•�â•�");
				CommandManager.sendChatMessage("Name: " + module.getName());
				CommandManager.sendChatMessage("Description: " + module.getDescription());
				CommandManager.sendChatMessage("Keybind: " + module.getBind().getTranslationKey());
			}
		}

	}

}
