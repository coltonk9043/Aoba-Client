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

    private static final int MAX_HUNGER = 20;

    public CmdAutoEat() {
        super("autoeat", "Automatically eats when the player is hungry.", "[toggle/set] [value]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        if (parameters == null || parameters.length != 2) {
            throw new InvalidSyntaxException(this);
        }

        AutoEat module = Aoba.getInstance().moduleManager.autoeat;

        switch (parameters[0].toLowerCase()) {
            case "toggle":
                toggleAutoEat(module, parameters[1]);
                break;
            case "set":
                setHunger(module, parameters[1]);
                break;
            default:
                throw new InvalidSyntaxException(this);
        }
    }

    private void toggleAutoEat(AutoEat module, String state) {
        if (state.equalsIgnoreCase("on")) {
            module.state.setValue(true);
            sendChatMessage("AutoEat toggled ON");
        } else if (state.equalsIgnoreCase("off")) {
            module.state.setValue(false);
            sendChatMessage("AutoEat toggled OFF");
        } else {
            sendChatMessage("Invalid value. [ON/OFF]");
        }
    }

    private void setHunger(AutoEat module, String setting) {
        try {
            int hunger = (int) Math.min(Double.parseDouble(setting) * 2, MAX_HUNGER);
            module.setHunger(hunger);
            sendChatMessage("AutoEat hunger set to " + setting + " hearts.");
        } catch (NumberFormatException e) {
            sendChatMessage("Invalid hunger value.");
        }
    }

    private void sendChatMessage(String message) {
        CommandManager.sendChatMessage(message);
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        if (previousParameter == null) {
            return new String[] { "toggle", "set" };
        }

        switch (previousParameter.toLowerCase()) {
            case "toggle":
                return new String[] { "on", "off" };
            case "set":
                return new String[] { "1", "2", "4", "6", "8" };
            default:
                return new String[] { "toggle", "set" };
        }
    }
}
