package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.combat.Aimbot;

public class CmdAimbot extends Command {

	public CmdAimbot() {
		this.description = "Allows the player to see chest locations through ESP";
	}

	@Override
	public void command(String[] parameters) {
		Aimbot module = (Aimbot) Aoba.getInstance().mm.aimbot;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Aimbot toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Aimbot toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba aimbot [toggle/mode] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba aimbot [toggle/mode] [value]");
		}
	}
}
