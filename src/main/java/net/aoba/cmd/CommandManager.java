package net.aoba.cmd;

import com.mojang.logging.LogUtils;
import net.aoba.Aoba;
import net.aoba.api.IAddon;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;
import java.util.*;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final List<String> commandHistory = new ArrayList<>();

    public static StringSetting PREFIX = new StringSetting("Prefix", "Prefix", ".aoba");

    public CommandManager(List<IAddon> addons) {
        SettingManager.registerSetting(PREFIX, Aoba.getInstance().settingManager.hiddenContainer);

        for (Field field : getClass().getDeclaredFields()) {
            if (Command.class.isAssignableFrom(field.getType())) {
                try {
                    Command cmd = (Command) field.get(this);
                    commands.put(cmd.getName(), cmd);
                } catch (IllegalAccessException e) {
                    LogUtils.getLogger().error("Error initializing Aoba commands: " + e.getMessage());
                }
            }
        }

        addons.forEach(addon -> addon.commands().forEach(command -> {
            if (!commands.containsKey(command.getName())) {
                commands.put(command.getName(), command);
            } else {
                LogUtils.getLogger().warn("Warning: Duplicate command name \"" + command.getName() + "\" from addon. This command will not be registered.");
            }
        }));
    }

    public Command getCommandBySyntax(String syntax) {
        return commands.get(syntax);
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public int getNumOfCommands() {
        return commands.size();
    }

    public void command(String[] commandIn) {
        try {
            commandHistory.add(String.join(" ", commandIn));

            Command command = commands.get(commandIn[1]);
            if (command == null) {
                sendChatMessage("Invalid Command! Type " + Formatting.LIGHT_PURPLE + ".aoba help" + Formatting.RESET + " for a list of commands.");
            } else {
                String[] parameterList = Arrays.copyOfRange(commandIn, 2, commandIn.length);
                command.runCommand(parameterList);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sendChatMessage("Invalid Command! Type " + Formatting.LIGHT_PURPLE + ".aoba help" + Formatting.RESET + " for a list of commands.");
        } catch (InvalidSyntaxException e) {
            e.PrintToChat();
        }
    }

    public List<String> getCommandHistory() {
        return commandHistory;
    }

    public static void sendChatMessage(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud != null) {
            mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.DARK_PURPLE + "[" + Formatting.LIGHT_PURPLE + "Aoba" + Formatting.DARK_PURPLE + "] " + Formatting.RESET + message));
        }
    }
}
