package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;

public class CmdTP extends Command {

	public CmdTP() {
		this.command = "tp";
		this.description = "Teleports the player certain blocks away (Vanilla only)";
	}

	@Override
	public void command(String[] parameters) {
		if (parameters.length == 3) {
			mc.player.setPosition(Double.parseDouble(parameters[0]), Double.parseDouble(parameters[1]), Double.parseDouble(parameters[2]));
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba tp [x] [y] [z]");
		}
	}
}
