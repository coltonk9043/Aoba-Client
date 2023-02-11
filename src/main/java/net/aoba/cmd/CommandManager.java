/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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

import java.util.ArrayList;
import java.util.HashMap;

import net.aoba.cmd.commands.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CommandManager {
	private HashMap<String, Command> commands = new HashMap <String, Command>();
	
	/**
	 * Constructor for Command Manager. Initializes all commands.
	 */
	public CommandManager() {
		commands.put("aimbot", new CmdAimbot());
		commands.put("autoeat", new CmdAutoEat());
		commands.put("chestesp", new CmdChestESP());
		commands.put("entityesp", new CmdEntityESP());
		commands.put("fastbreak", new CmdFastBreak());
		commands.put("fly", new CmdFly());
		commands.put("freecam", new CmdFreecam());
		commands.put("fullbright", new CmdFullbright());
		commands.put("help", new CmdHelp());
		commands.put("hud", new CmdHud());
		commands.put("itemesp", new CmdItemESP());
		commands.put("noclip", new CmdNoclip());
		commands.put("nofall", new CmdNoFall());
		commands.put("noslowdown", new CmdNoSlowdown());
		commands.put("nuker", new CmdNuker());
		commands.put("playeresp", new CmdPlayerESP());
		commands.put("pov", new CmdPOV());
		commands.put("reach", new CmdReach());
		commands.put("spam", new CmdSpam());
		commands.put("sprint", new CmdSprint());
		commands.put("step", new CmdStep());
		commands.put("tilebreaker", new CmdTileBreaker());
		commands.put("timer", new CmdTimer());
		commands.put("tp", new CmdTP());
		commands.put("tracer", new CmdTracer());
		commands.put("xray", new CmdXRay());
	}
	
	public Command getCommandBySyntax(String string) {
		return this.commands.get(string);
	}

	public HashMap<String, Command> getCommands(){
		return this.commands;
	}
	
	public int getNumOfCommands() {
		return this.commands.size();
	}
	
	public void command(String[] commandIn) {
		try {
			Command command = commands.get(commandIn[1]);
			
			if(command == null) 
				sendChatMessage("Invalid Command! Type .aoba help for a list of commands.");	
			else {
				String[] parameterList = new String[commandIn.length - 2];
				if(commandIn.length > 1) {
					for(int i=2; i < commandIn.length; i++) {
						parameterList[i-2]= commandIn[i];
					}
				}
				command.command(parameterList);
			}
		}catch(Exception e){
			sendChatMessage("Error occured whilst running command. Please try again.");
			e.printStackTrace();
		}
	}

	public static void sendChatMessage(String message) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.inGameHud.getChatHud().addMessage(Text.of("§5[Aoba] §f" + message));
	}
}
