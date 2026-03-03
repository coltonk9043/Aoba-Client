/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.command;

import net.aoba.managers.CommandManager;
import net.minecraft.ChatFormatting;
import java.io.Serial;

public class InvalidSyntaxException extends CommandException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidSyntaxException(Command cmd) {
        super(cmd);
    }

    @Override
    public void PrintToChat() {
        CommandManager.sendChatMessage("Invalid syntax! Correct usage: " + ChatFormatting.LIGHT_PURPLE + ".aoba " + cmd.getName() + " " + cmd.getSyntax() + ChatFormatting.RESET);
    }
}
