/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.command.commands;

import net.aoba.Aoba;
import net.aoba.command.Command;
import net.aoba.managers.CommandManager;
import net.aoba.command.InvalidSyntaxException;
import net.aoba.module.modules.misc.AutoEat;

public class CmdAutoEat extends Command {

	public CmdAutoEat() {
		super("autoeat", "Automatically eats when the player is hungry.", "[toggle/set] [value]");
	}

	@Override
	public void runCommand(String[] parameters) throws InvalidSyntaxException {
		if (parameters.length != 2)
			throw new InvalidSyntaxException(this);

		AutoEat module = Aoba.getInstance().moduleManager.autoeat;

		switch (parameters[0]) {
		case "toggle":
			String state = parameters[1].toLowerCase();
			if (state.equals("on")) {
				module.state.setValue(true);
				CommandManager.sendChatMessage("AutoEat toggled ON");
			} else if (state.equals("off")) {
				module.state.setValue(false);
				CommandManager.sendChatMessage("AutoEat toggled OFF");
			} else {
				CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
			}
			break;
		case "set":
			String setting = parameters[1].toLowerCase();
			if (setting.isEmpty()) {
				CommandManager.sendChatMessage("Please enter the number of hearts to set to.");
			} else {
				module.setHunger((int) Math.min(Double.parseDouble(setting) * 2, 20));
				CommandManager.sendChatMessage("AutoEat hunger set to " + setting + " hearts.");
			}
			break;
		default:
			throw new InvalidSyntaxException(this);
		}
	}

	@Override
	public String[] getAutocorrect(String previousParameter) {
		switch (previousParameter) {
		case "toggle":
			return new String[] { "on", "off" };
		case "set":
			return new String[] { "1", "2", "4", "6", "8" };
		default:
			return new String[] { "toggle", "set" };
		}
	}
}