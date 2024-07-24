/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.cmd.commands;

import net.aoba.cmd.Command;
import net.aoba.cmd.InvalidSyntaxException;

public class CmdSpam extends Command {

    public CmdSpam() {
        super("spam", "Spams the chat with a certain message.", "[times] [message]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        if (parameters.length < 2)
            throw new InvalidSyntaxException(this);

        // Combines the "parameters" into a string to be printed.
        StringBuilder message = new StringBuilder();
        for (int msg = 1; msg < parameters.length; msg++) {
            message.append(parameters[msg]).append(" ");
        }

        // Prints out that message X number of times.
        for (int i = 0; i < Integer.parseInt(parameters[0]); i++) {
            mc.player.networkHandler.sendChatMessage(message.toString());
        }

    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            default:
                return new String[]{"Aoba is an amazing client!"};
        }
    }
}
