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

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.InvalidSyntaxException;
import net.minecraft.client.util.InputUtil;

public class CmdClickgui extends Command {

    public CmdClickgui() {
        super("clickgui", "Allows the player to see chest locations through ESP", "[set/open] [value]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        switch (parameters[0]) {
            case "set":
                if (parameters.length != 2)
                    throw new InvalidSyntaxException(this);
                char keybind = Character.toUpperCase(parameters[1].charAt(0));
                Aoba.getInstance().guiManager.clickGuiButton.setValue(InputUtil.fromKeyCode(keybind, 0));
                break;
            case "open":
                Aoba.getInstance().guiManager.setClickGuiOpen(true);
                break;
            default:
                throw new InvalidSyntaxException(this);
        }
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            default:
                return new String[]{"set", "open"};
        }
    }
}
