/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.command;

import net.aoba.AobaClient;
import net.minecraft.client.MinecraftClient;

import java.util.Objects;

public abstract class Command {
    protected final String name;
    protected final String description;
    protected final String syntax;

    protected static final MinecraftClient mc = AobaClient.MC;

    public Command(String name, String description, String syntax) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.syntax = Objects.requireNonNull(syntax);
    }

    /**
     * Gets the name of the command.
     *
     * @return The name of the command.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the command.
     *
     * @return The description of the command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the syntax of the command.
     *
     * @return The syntax of the command.
     */
    public String getSyntax() {
        return syntax;
    }

    /**
     * Runs the intended action of the command.
     *
     * @param parameters The parameters being passed.
     */
    public abstract void runCommand(String[] parameters) throws InvalidSyntaxException;

    /**
     * Gets the next Autocorrect suggestions given the last typed parameter.
     *
     * @param previousParameter
     */
    public abstract String[] getAutocorrect(String previousParameter);
}
