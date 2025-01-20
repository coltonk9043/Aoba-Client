/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.command.commands;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.command.Command;
import net.aoba.managers.CommandManager;
import net.aoba.command.InvalidSyntaxException;
import net.minecraft.client.font.TextRenderer;

public class CmdFont extends Command {

    public CmdFont() {
        super("font", "Sets the HUD font.", "[set] [value]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        if (parameters.length != 2)
            throw new InvalidSyntaxException(this);

        AobaClient aoba = Aoba.getInstance();

        switch (parameters[0]) {
            case "set":
                try {
                    String font = parameters[1];
                    TextRenderer t = aoba.fontManager.fontRenderers.get(font);
                    if (t != null) {
                        aoba.fontManager.SetRenderer(t);
                    }
                } catch (Exception e) {
                    CommandManager.sendChatMessage("Invalid value.");
                }
                break;
            default:
                throw new InvalidSyntaxException(this);
        }
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            case "set":
                AobaClient aoba = Aoba.getInstance();

                String[] suggestions = new String[aoba.fontManager.fontRenderers.size()];

                int i = 0;
                for (String fontName : aoba.fontManager.fontRenderers.keySet())
                    suggestions[i++] = fontName;

                return suggestions;
            default:
                return new String[]{"set"};
        }
    }
}