package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.movement.Sprint;

public class CmdSprint extends Command {

	public CmdSprint() {
		this.command = "sprint";
		this.description = "Forces the player to constantly sprint";
	}

	@Override
	public void command(String[] parameters) {
		Sprint module = (Sprint) Aoba.getInstance().mm.sprint;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Sprint toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Sprint toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba sprint [toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba sprint [toggle] [value]");
		}
	}
}
