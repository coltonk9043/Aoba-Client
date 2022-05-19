package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.combat.Reach;

public class CmdReach extends Command {

	public CmdReach() {
		this.command = "reach";
		this.description = "Allows the player to reach further.";
	}

	@Override
	public void command(String[] parameters) {
		Reach module = (Reach) Aoba.getInstance().mm.reach;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "distance":
				try {
					float distance = Float.parseFloat(parameters[1]);
					module.setReachLength(distance);
					CommandManager.sendChatMessage("Reach distance set to " + distance);

				} catch (Exception e) {
					CommandManager.sendChatMessage("Invalid value.");
				}
				break;
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Reach toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Reach toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba reach [toggle/distance] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba reach [toggle/distance] [value]");
		}
	}
}