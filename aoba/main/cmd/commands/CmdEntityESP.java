package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;
import aoba.main.module.modules.render.EntityESP;

public class CmdEntityESP extends Command {

	public CmdEntityESP() {
		this.command = "entityesp";
		this.description = "Allows the player to see mobs through ESP.";
	}

	@Override
	public void command(String[] parameters) {
		EntityESP module = (EntityESP) mc.aoba.mm.entityesp;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("EntityESP toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("EntityESP toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba entityesp [toggle] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba entityesp [toggle] [value]");
		}
	}
}
