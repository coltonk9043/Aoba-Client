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

/**
 * A class to represent a system to manage Commands.
 */
package net.aoba.cmd;

import java.lang.reflect.Field;
import java.util.HashMap;
import net.aoba.Aoba;
import net.aoba.cmd.commands.*;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandManager {
	private HashMap<String, Command> commands = new HashMap<String, Command>();

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
	
	public static StringSetting PREFIX;
	
	/**
	 * Constructor for Command Manager. Initializes all commands.
	 */
	public CommandManager() {
		
		PREFIX = new StringSetting("Prefix", "Prefix", ".aoba");
		 
		SettingManager.registerSetting(PREFIX, Aoba.getInstance().settingManager.hidden_category);
		
		try
		{
			for(Field field : CommandManager.class.getDeclaredFields())
			{
				if (!Command.class.isAssignableFrom(field.getType())) 
					continue;
				Command cmd = (Command)field.get(this);
				commands.put(cmd.getName(), cmd);
			}
		}catch(Exception e)
		{
			System.out.println("Error initializing Aoba commands.");
			System.out.println(e.getStackTrace().toString());
		}
	}

	/**
	 * Gets the command by a given syntax.
	 * 
	 * @param string The syntax (command) as a string.
	 * @return The Command Object associated with that syntax.
	 */
	public Command getCommandBySyntax(String string) {
		return this.commands.get(string);
	}

	/**
	 * Gets all of the Commands currently registered.
	 * 
	 * @return List of registered Command Objects.
	 */
	public HashMap<String, Command> getCommands() {
		return this.commands;
	}

	/**
	 * Gets the total number of Commands.
	 * @return The number of registered Commands.
	 */
	public int getNumOfCommands() {
		return this.commands.size();
	}

	/**
	 * Runs a command.
	 * @param commandIn A list of Command Parameters given by a "split" message.
	 */
	public void command(String[] commandIn) {
		try {
			
			// Get the command from the user's message. (Index 0 is Username)
			Command command = commands.get(commandIn[1]);

			// If the command does not exist, throw an error.
			if (command == null)
				sendChatMessage("Invalid Command! Type " + Formatting.LIGHT_PURPLE + ".aoba help" + Formatting.RESET + " for a list of commands.");
			else {
				// Otherwise, create a new parameter list.
				String[] parameterList = new String[commandIn.length - 2];
				if (commandIn.length > 1) {
					for (int i = 2; i < commandIn.length; i++) {
						parameterList[i - 2] = commandIn[i];
					}
				}
				
				// Runs the command.
				command.runCommand(parameterList);
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			sendChatMessage("Invalid Command! Type " + Formatting.LIGHT_PURPLE + ".aoba help" + Formatting.RESET + " for a list of commands.");
		} catch (InvalidSyntaxException e) {
			e.PrintToChat();
		}
	}

	/**
	 * Prints a message into the Minecraft Chat.
	 * @param message The message to be printed.
	 */
	public static void sendChatMessage(String message) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if(mc.inGameHud != null) {
			mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.DARK_PURPLE + "[" + Formatting.LIGHT_PURPLE + "Aoba" + Formatting.DARK_PURPLE +  "] " + Formatting.RESET + message));
		}
	}
}
