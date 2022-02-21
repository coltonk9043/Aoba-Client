package net.aoba.cmd.commands;

import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;

public class CmdHud extends Command {

	public CmdHud() {
		this.command = "hud";
		this.description = "Allows you to customize the hud using commands.";
	}

	@Override
	public void command(String[] parameters) {
		if (parameters.length == 2) {
			switch (parameters[0]) {
			
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba hud");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba hud");
		}
	}
}
