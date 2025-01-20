/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.command;

import java.io.Serial;

public abstract class CommandException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;
    protected Command cmd;

    public CommandException(Command cmd) {
        this.cmd = cmd;
    }

    public abstract void PrintToChat();
}
