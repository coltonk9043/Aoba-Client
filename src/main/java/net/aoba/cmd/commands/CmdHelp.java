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
import net.aoba.cmd.CommandManager;
import net.aoba.module.Module;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class CmdHelp extends Command {

    int indexesPerPage = 5;

    public CmdHelp() {
        super("help", "Shows the avaiable commands.", "[page, module]");
    }

    @Override
    public void runCommand(String[] parameters) {
        if (parameters.length <= 0) {
            ShowCommands(1);
        } else if (StringUtils.isNumeric(parameters[0])) {
            int page = Integer.parseInt(parameters[0]);
            ShowCommands(page);
        } else {
            Module module = Aoba.getInstance().moduleManager.getModuleByName(parameters[0]);
            if (module == null) {
                CommandManager.sendChatMessage("Could not find Module '" + parameters[0] + "'.");
            } else {
                String title = "------------ " + Formatting.LIGHT_PURPLE + module.getName() + " Help" + Formatting.RESET + " ------------";
                String unformatted_title = "------------ " + module.getName() + " Help ------------";
                CommandManager.sendChatMessage(title);
                CommandManager.sendChatMessage("Name: " + Formatting.LIGHT_PURPLE + module.getName() + Formatting.RESET);
                CommandManager.sendChatMessage("Description: " + Formatting.LIGHT_PURPLE + module.getDescription() + Formatting.RESET);
                CommandManager.sendChatMessage("Keybind: " + Formatting.LIGHT_PURPLE + module.getBind().getValue().getTranslationKey() + Formatting.RESET);
                CommandManager.sendChatMessage("-".repeat(unformatted_title.length() - 2)); // mc font characters are not the same width but eh..
            }
        }

    }

    private void ShowCommands(int page) {
        String title = "------------ Help [Page " + page + " of 5] ------------";  // TODO: remove hardcoded page length
        CommandManager.sendChatMessage(title);
        CommandManager.sendChatMessage("Use " + Formatting.LIGHT_PURPLE + ".aoba help [n]" + Formatting.RESET + " to get page n of help.");

        // Fetch the commands and dislays their syntax on the screen.
        Map<String, Command> commands = Aoba.getInstance().commandManager.getCommands();
        Set<String> keySet = commands.keySet();
        ArrayList<String> listOfCommands = new ArrayList<String>(keySet);

        for (int i = (page - 1) * indexesPerPage; i <= (page * indexesPerPage); i++) {
            if (i >= 0 && i < Aoba.getInstance().commandManager.getNumOfCommands()) {
                CommandManager.sendChatMessage(" .aoba " + listOfCommands.get(i));
            }
        }
        CommandManager.sendChatMessage("-".repeat(title.length() - 2)); // mc font characters are not the same width but eh..
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        CommandManager cm = Aoba.getInstance().commandManager;
        int numCmds = cm.getNumOfCommands();
        String[] commands = new String[numCmds];

        Set<String> cmds = Aoba.getInstance().commandManager.getCommands().keySet();
        int i = 0;
        for (String x : cmds)
            commands[i++] = x;

        return commands;
    }

}
