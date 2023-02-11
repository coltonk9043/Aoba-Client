package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.module.modules.world.TileBreaker;

public class CmdTileBreaker extends Command {

	public CmdTileBreaker() {
		this.description = "Breaks insta-break blocks within a certain radius";
	}

	@Override
	public void command(String[] parameters) {
		TileBreaker module = (TileBreaker) Aoba.getInstance().mm.tilebreaker;
		if (parameters.length == 2) {
			switch (parameters[0]) {
			case "radius":
				try {
					int radius = Integer.parseInt(parameters[1]);
					if (radius > 10) {
						radius = 10;
					} else if (radius < 1) {
						radius = 1;
					}
					module.setRadius(radius);
					CommandManager.sendChatMessage("TileBreaker radius set to " + radius);

				} catch (Exception e) {
					CommandManager.sendChatMessage("Invalid value. [1-10]");
				}
				break;
			case "toggle":
				String state = parameters[1].toLowerCase();
				if (state.equals("on")) {
					module.setState(true);
					CommandManager.sendChatMessage("TileBreaker toggled ON");
				} else if (state.equals("off")) {
					module.setState(false);
					CommandManager.sendChatMessage("TileBreaker toggled OFF");
				} else {
					CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
				}
				break;
			default:
				CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba tilebreaker [radius, toggle] [value]");
				break;
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Usage: .aoba tilebreaker [radius, toggle] [value]");
		}
	}
}
