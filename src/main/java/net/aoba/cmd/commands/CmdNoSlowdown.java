package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.movement.NoSlowdown;

public class CmdNoSlowdown extends Command {

	public CmdNoSlowdown() {
		this.description = "Disables webs from slowing the player down";
	}

	@Override
	public void command(String[] parameters) {
		NoSlowdown module = (NoSlowdown) Aoba.getInstance().mm.noslowdown;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("NoSlowDown toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("NoSlowDown toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba noslowdown [toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba noslowdown [toggle] [value]");
		}
	}

}
