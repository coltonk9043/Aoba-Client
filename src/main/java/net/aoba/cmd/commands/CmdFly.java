package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.movement.Fly;

public class CmdFly extends Command {

	public CmdFly() {
		this.description = "Allows the player to fly";
	}

	@Override
	public void command(String[] parameters) {
		for(int i = 0; i < parameters.length; i++) {
			System.out.println(parameters[i]);
		}
		Fly module = (Fly) Aoba.getInstance().mm.fly;
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
