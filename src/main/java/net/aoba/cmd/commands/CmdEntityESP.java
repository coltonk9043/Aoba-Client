package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.render.EntityESP;

public class CmdEntityESP extends Command {

	public CmdEntityESP() {
		this.description = "Allows the player to see mobs through ESP.";
	}

	@Override
	public void command(String[] parameters) {
		EntityESP module = (EntityESP) Aoba.getInstance().mm.entityesp;
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
