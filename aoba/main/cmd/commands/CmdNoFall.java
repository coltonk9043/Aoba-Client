package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;
import aoba.main.module.modules.movement.NoFall;

public class CmdNoFall extends Command {

	public CmdNoFall() {
		this.command = "nofall";
		this.description = "Disables fall damage for the player";
	}

	@Override
	public void command(String[] parameters) {
		NoFall module = (NoFall) mc.aoba.mm.nofall;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("NoFall toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("NoFall toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba nofall [toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba nofall [toggle] [value]");
		}
	}

}
