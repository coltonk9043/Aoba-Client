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
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;

public class CmdHistory extends Command {

    private static final int HISTORY_PER_PAGE = 5;

    public CmdHistory() {
        super("history", "Shows the command history.", "[page]");
    }

    @Override
    public void runCommand(String[] parameters) {
        if (parameters.length == 0) {
            showHistory(1);
        } else if (StringUtils.isNumeric(parameters[0])) {
            int page = Integer.parseInt(parameters[0]);
            showHistory(page);
        } else {
            CommandManager.sendChatMessage("Invalid parameter. Usage: .aoba history [page]");
        }
    }

    private void showHistory(int page) {
        CommandManager cm = Aoba.getInstance().commandManager;
        ArrayList<String> history = new ArrayList<>(cm.getCommandHistory());
        int totalPages = (int) Math.ceil((double) history.size() / HISTORY_PER_PAGE);

        if (page < 1 || page > totalPages) {
            CommandManager.sendChatMessage("Page " + page + " does not exist. There are only " + totalPages + " pages.");
            return;
        }

        String title = "------------ Command History [Page " + page + " of " + totalPages + "] ------------";
        CommandManager.sendChatMessage(title);

        int startIndex = (page - 1) * HISTORY_PER_PAGE;
        int endIndex = Math.min(startIndex + HISTORY_PER_PAGE, history.size());

        for (int i = startIndex; i < endIndex; i++) {
            CommandManager.sendChatMessage((i + 1) + ": " + history.get(i));
        }

        CommandManager.sendChatMessage("-".repeat(title.length() - 2)); // Adjust for Minecraft font width
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        return new String[0]; // No autocorrect needed for history command
    }
}
