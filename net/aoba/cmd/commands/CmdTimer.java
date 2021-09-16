package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.misc.Timer;

public class CmdTimer extends Command {

	public CmdTimer() {
		this.command = "timer";
		this.description = "Disables fall damage for the player";
	}

	@Override
	public void command(String[] parameters) {
		Timer module = (Timer) Aoba.getInstance().mm.timer;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Timer toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Timer toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			case "multiplier":
				try {
					float param1 = Float.parseFloat(parameters[1]);
					// TODO FIX TIMER MULTIPLER SET USING MIXIN
					//mc.timer.setMultiplier(param1);
					CommandManager.sendChatMessage("Timer multiplier set to " + param1);

				} catch (Exception e) {
					CommandManager.sendChatMessage("Invalid value.");
				}
				break;

			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba timer [toggle/multiplier] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba timer [toggle] [value]");
		}
	}

}