package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;

public class CmdHelp extends Command {

	int indexesPerPage = 5;

	public CmdHelp() {
		this.command = "help";
		this.description = "Shows the avaiable commands.";
	}

	@Override
	public void command(String[] parameters) {
		if (parameters.length == 1) {
			CommandManager.sendChatMessage("-------------------------------");

			for (int i = (Integer.parseInt(parameters[0]) - 1)
					* indexesPerPage; i <= (Integer.parseInt(parameters[0]) - 1) * indexesPerPage
							+ indexesPerPage; i++) {
				try {
					if (!(i > mc.aoba.cm.commands.size())) {
						CommandManager
								.sendChatMessage(" .aoba " + mc.aoba.cm.commands.get(i).getCommand()
										+ " : " + mc.aoba.cm.commands.get(i).getDescription());
					}
				} catch (Exception e) {

				}
			}
			CommandManager.sendChatMessage("---------= PAGE " + Integer.parseInt(parameters[0]) + " of 4 =---------");

		} else {
			for (int i = 0; i < indexesPerPage; i++) {
				CommandManager.sendChatMessage(" .aoba " + mc.aoba.cm.commands.get(i).getCommand()
						+ " : " + mc.aoba.cm.commands.get(i).getDescription());
			}
		}
	}
}
