package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;
import aoba.main.module.modules.misc.AutoEat;

public class CmdAutoEat extends Command {

	public CmdAutoEat() {
		this.command = "autoeat";
		this.description = "Allows the player to see chest locations through ESP";
	}

	@Override
	public void command(String[] parameters) {
		AutoEat module = (AutoEat) mc.aoba.mm.autoeat;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("AutoEat toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("AutoEat toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			case "set":
				String setting = parameters[1].toLowerCase();
				if(setting.isEmpty()) {
					CommandManager.sendChatMessage("Please enter the number of hearts to set to.");
				}else {
					module.setHunger((int)Math.min(Double.parseDouble(setting) * 2, 20));
					CommandManager.sendChatMessage("AutoEat hunger set to " + setting + " hearts.");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba autoeat [toggle/set] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba autoeat [toggle/set] [value]");
		}
	}
}