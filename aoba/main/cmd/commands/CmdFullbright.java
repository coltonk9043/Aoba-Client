package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;
import aoba.main.module.modules.render.Fullbright;

public class CmdFullbright extends Command {

	public CmdFullbright() {
		this.command = "fullbright";
		this.description = "Brightens up the world!";
	}

	@Override
	public void command(String[] parameters) {
		Fullbright module = (Fullbright) mc.aoba.mm.fullbright;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Fullbright toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Fullbright toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba fullbright [toggle] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba fullbright [toggle] [value]");
		}
	}
}
