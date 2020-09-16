package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;
import aoba.main.module.modules.render.ChestESP;

public class CmdChestESP extends Command {

	public CmdChestESP() {
		this.command = "chestesp";
		this.description = "Allows the player to see chest locations through ESP";
	}

	@Override
	public void command(String[] parameters) {
		ChestESP module = (ChestESP) mc.aoba.mm.chestesp;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("ChestESP toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("ChestESP toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba chestesp [toggle] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba chestesp [toggle] [value]");
		}
	}
}
