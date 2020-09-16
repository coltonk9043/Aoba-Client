package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;
import aoba.main.module.modules.movement.Noclip;

public class CmdNoclip extends Command {

	public CmdNoclip() {
		this.command = "noclip";
		this.description = "Allows the player to phase through blocks.";
	}

	@Override
	public void command(String[] parameters) {
		Noclip module = (Noclip) mc.aoba.mm.noclip;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "speed":
				try {
					float speed = Float.parseFloat(parameters[1]);
					module.setSpeed(speed);
					CommandManager.sendChatMessage("Flight speed set to " + speed);

				} catch (Exception e) {
					CommandManager.sendChatMessage("Invalid value.");
				}
				break;
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Noclip toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Noclip toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba noclip [toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba noclip [toggle] [value]");
		}
	}

}
