package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;
import aoba.main.module.modules.movement.Fly;

public class CmdFly extends Command {

	public CmdFly() {
		this.command = "fly";
		this.description = "Allows the player to fly";
	}

	@Override
	public void command(String[] parameters) {
		Fly module = (Fly) mc.aoba.mm.fly;
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
					CommandManager.sendChatMessage("Fly toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Fly toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba fly [speed, toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba fly [speed, toggle] [value]");
		}
	}
}
