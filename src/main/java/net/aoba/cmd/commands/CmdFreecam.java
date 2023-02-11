package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.movement.Freecam;

public class CmdFreecam extends Command {

	public CmdFreecam() {
		this.command = "freecam";
		this.description = "Disables fall damage for the player";
	}

	@Override
	public void command(String[] parameters) {
		Freecam module = (Freecam) Aoba.getInstance().mm.freecam;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Freecam toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Freecam toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba freecam [toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba freecam [toggle] [value]");
		}
	}

}
