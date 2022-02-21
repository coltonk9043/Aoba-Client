package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.render.POV;

public class CmdPOV extends Command {

	public CmdPOV() {
		this.command = "pov";
		this.description = "Decreases the time it takes to break blocks";
	}

	@Override
	public void command(String[] parameters) {
		POV module = (POV) Aoba.getInstance().mm.pov;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "set":
				try {
					String player = parameters[1];
					CommandManager.sendChatMessage("Setting POV Player Name to " + player);
					module.setEntityPOV(player);
				} catch (Exception e) {
					CommandManager.sendChatMessage("Invalid value.");
				}
				break;
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("POV toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("POV toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba pov [set, toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba pov [set, toggle] [value]");
		}
	}
}