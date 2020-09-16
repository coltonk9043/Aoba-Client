package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;
import aoba.main.module.modules.combat.Aimbot;

public class CmdAimbot extends Command {

	public CmdAimbot() {
		this.command = "aimbot";
		this.description = "Allows the player to see chest locations through ESP";
	}

	@Override
	public void command(String[] parameters) {
		Aimbot module = (Aimbot) mc.aoba.mm.aimbot;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Aimbot toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Aimbot toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			case "mode":
				String mode = parameters[1].toLowerCase();
				if (mode.equals("entity")) {
					module.changeMode(Aimbot.Mode.ENTITY);
					CommandManager.sendChatMessage("Aimbot mode set to Entities only");
				} else if (mode.equals("player")) {
					module.changeMode(Aimbot.Mode.PLAYER);
					CommandManager.sendChatMessage("Aimbot mode set to Players only");
				} else if (mode.equals("both")) {
					module.changeMode(Aimbot.Mode.BOTH);
					CommandManager.sendChatMessage("Aimbot mode set to both Entities and Players");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba aimbot [toggle/mode] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba aimbot [toggle/mode] [value]");
		}
	}
}
