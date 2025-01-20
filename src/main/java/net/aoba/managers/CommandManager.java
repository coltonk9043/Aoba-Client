/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.managers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.logging.LogUtils;

import net.aoba.api.IAddon;
import net.aoba.command.Command;
import net.aoba.command.InvalidSyntaxException;
import net.aoba.command.commands.*;
import net.aoba.settings.types.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandManager {
	private final Map<String, Command> commands = new HashMap<>();
	private final List<String> commandHistory = new ArrayList<>();

	public static StringSetting PREFIX = StringSetting.builder().id("aoba_prefix").displayName("Prefix")
			.defaultValue(".aoba").build();

	// Commands
	public final CmdAimbot aimbot = new CmdAimbot();
	public final CmdAutoEat autoeat = new CmdAutoEat();
	public final CmdChestESP chestesp = new CmdChestESP();
	public final CmdClickgui clickgui = new CmdClickgui();
	public final CmdEntityESP entityesp = new CmdEntityESP();
	public final CmdFastBreak fastbreak = new CmdFastBreak();
	public final CmdFly fly = new CmdFly();
	public final CmdFreecam freecam = new CmdFreecam();
	public final CmdFont font = new CmdFont();
	public final CmdFullbright fullbright = new CmdFullbright();
	public final CmdHelp help = new CmdHelp();
	public final CmdHud hud = new CmdHud();
	public final CmdItemESP itemesp = new CmdItemESP();
	public final CmdNoclip noclip = new CmdNoclip();
	public final CmdNoFall nofall = new CmdNoFall();
	public final CmdNoSlowdown noslowdown = new CmdNoSlowdown();
	public final CmdNuker nuker = new CmdNuker();
	public final CmdPlayerESP playeresp = new CmdPlayerESP();
	public final CmdPOV pov = new CmdPOV();
	public final CmdReach reach = new CmdReach();
	public final CmdSpam spam = new CmdSpam();
	public final CmdSprint sprint = new CmdSprint();
	public final CmdStep step = new CmdStep();
	public final CmdTileBreaker tilebreaker = new CmdTileBreaker();
	public final CmdTimer timer = new CmdTimer();
	public final CmdTP tp = new CmdTP();
	public final CmdTracer tracer = new CmdTracer();
	public final CmdXRay xray = new CmdXRay();
	public final CmdHistory history = new CmdHistory();
	public final CmdDiscord discord = new CmdDiscord();

	public CommandManager(List<IAddon> addons) {
		SettingManager.registerSetting(PREFIX);

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
				LogUtils.getLogger().warn("Warning: Duplicate command name \"" + command.getName()
						+ "\" from addon. This command will not be registered.");
			}
		}));
	}

	/** Gets the command object from a syntax. */
	public Command getCommandBySyntax(String syntax) {
		return commands.get(syntax);
	}

	/**
	 * Gets all of the Commands currently registered, including ones registered by
	 * addons.
	 */
	public Map<String, Command> getCommands() {
		return commands;
	}

	/**
	 * Gets the total number of registered Commands, including ones registered by
	 * addons
	 */
	public int getNumOfCommands() {
		return commands.size();
	}

	/** Runs a command. */
	public void command(String[] commandIn) {
		try {
			commandHistory.add(String.join(" ", commandIn));

			Command command = commands.get(commandIn[1]);
			if (command == null) {
				sendChatMessage("Invalid Command! Type " + Formatting.LIGHT_PURPLE + ".aoba help" + Formatting.RESET
						+ " for a list of commands.");
			} else {
				String[] parameterList = Arrays.copyOfRange(commandIn, 2, commandIn.length);
				command.runCommand(parameterList);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			sendChatMessage("Invalid Command! Type " + Formatting.LIGHT_PURPLE + ".aoba help" + Formatting.RESET
					+ " for a list of commands.");
		} catch (InvalidSyntaxException e) {
			e.PrintToChat();
		}
	}

	/** Returns the command history. */
	public List<String> getCommandHistory() {
		return commandHistory;
	}

	/** Prints a message into the Minecraft Chat. */
	public static void sendChatMessage(String message) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.inGameHud != null) {
			mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.DARK_PURPLE + "[" + Formatting.LIGHT_PURPLE + "Aoba"
					+ Formatting.DARK_PURPLE + "] " + Formatting.RESET + message));
		}
	}
}
