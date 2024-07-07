package net.aoba.cmd.commands;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.cmd.InvalidSyntaxException;
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