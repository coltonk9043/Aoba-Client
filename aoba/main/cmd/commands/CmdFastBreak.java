package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;
import aoba.main.module.modules.misc.FastBreak;

public class CmdFastBreak extends Command {

	public CmdFastBreak() {
		this.command = "fastbreak";
		this.description = "Decreases the time it takes to break blocks";
	}

	@Override
	public void command(String[] parameters) {
		FastBreak module = (FastBreak) mc.aoba.mm.fastbreak;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "multiplier":
				try {
					float multiplier = Float.parseFloat(parameters[1]);
					module.setMultiplier(multiplier);
					module.toggle();
					CommandManager.sendChatMessage("FastBreak multiplier set to " + multiplier + "x");
					module.toggle();
				} catch (Exception e) {
					CommandManager.sendChatMessage("Invalid value.");
				}
				break;
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("FastBreak toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("FastBreak toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba fastbreak [multiplier, toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba fastbreak [multiplier, toggle] [value]");
		}
	}
}
