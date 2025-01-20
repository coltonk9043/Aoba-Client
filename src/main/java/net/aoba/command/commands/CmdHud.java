/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.command.commands;

import net.aoba.command.Command;
import net.aoba.command.InvalidSyntaxException;

public class CmdHud extends Command {

    public CmdHud() {
        super("hud", "Allows you to customize the hud using commands.", "[toggle] [value]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        if (parameters.length != 2)
            throw new InvalidSyntaxException(this);

        switch (parameters[0]) {
            default:
                throw new InvalidSyntaxException(this);
        }
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            case "toggle":
                return new String[]{"on", "off"};
            default:
                return new String[]{"toggle"};
        }
    }
}
