package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;
import aoba.main.module.modules.movement.Step;

public class CmdStep extends Command {

	public CmdStep() {
		this.command = "step";
		this.description = "Allows the player to step up blocks";
	}

	@Override
	public void command(String[] parameters) {
		Step module = (Step) mc.aoba.mm.step;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "height":
				try {
					float height = Float.parseFloat(parameters[1]);
					module.setStepHeight(height);
					CommandManager.sendChatMessage("Step height set to " + height);

				} catch (Exception e) {
					CommandManager.sendChatMessage("Invalid value.");
				}
				break;
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Step toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Step toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba step [toggle/height] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba step [toggle/height] [value]");
		}
	}
}
