package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.render.Tracer;

public class CmdTracer extends Command {

	public CmdTracer() {
		this.command = "tracer";
		this.description = "Draws a tracer that points towards players";
	}

	@Override
	public void command(String[] parameters) {
		Tracer module = (Tracer) Aoba.getInstance().mm.tracer;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("Tracer toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("Tracer toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba tracer [toggle] [value]");
				break;
			}
		} else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba tracer [toggle] [value]");
		}
	}
}
