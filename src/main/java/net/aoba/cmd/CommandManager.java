package net.aoba.cmd;

import java.util.ArrayList;
import net.aoba.cmd.commands.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CommandManager {
	public ArrayList<Command> commands = new ArrayList<Command>();
	
	int help = addCommand(new CmdHelp());
	int entityesp = addCommand(new CmdEntityESP());
	int chestesp = addCommand(new CmdChestESP());
	int fastbreak = addCommand(new CmdFastBreak());
	int fly = addCommand(new CmdFly());
	int freecam = addCommand(new CmdFreecam());
	int fullbright = addCommand(new CmdFullbright());
	int itemesp = addCommand(new CmdItemESP());
	int noclip = addCommand(new CmdNoclip());
	int nofall = addCommand(new CmdNoFall());
	int noslowdown = addCommand(new CmdNoSlowdown());
	int nuker = addCommand(new CmdNuker());
	int pov = addCommand(new CmdPOV());
	int playeresp = addCommand(new CmdPlayerESP());
	int reach = addCommand(new CmdReach());
	int spam = addCommand(new CmdSpam());
	int sprint = addCommand(new CmdSprint());
	int step = addCommand(new CmdStep());
	int tilebreaker = addCommand(new CmdTileBreaker());
	int tp = addCommand(new CmdTP());
	int tracer = addCommand(new CmdTracer());
	int xray = addCommand(new CmdXRay());
	int timer = addCommand(new CmdTimer());
	int aimboxt = addCommand(new CmdAimbot());
	
	public CommandManager() {

	}

	public int addCommand(Command command) {
		commands.add(command);
		return commands.size() - 1;
	}

	public Command getCommandById(int id) {
		return commands.get(id);
	}

	public void removeCommandById(int id) {
		commands.remove(id);
	}

	public void command(String[] commandIn) {
		for (Command command : commands) {
			try {
				if (commandIn[1].contains(command.getCommand())) {
					ArrayList<String> parameterList = new ArrayList<String>();
					for(int i=2; i < commandIn.length; i++) {
						parameterList.add(commandIn[i]);
						
					}
					command.command(parameterList.toArray(new String[0]));
					return;
				}
				
			}catch(Exception e){
				// If anyone ever sees this, I got lazy and didn't bother making a command for the
				// default syntax... 
				sendChatMessage("Ã¢â€�Å’Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�ï¿½");
				sendChatMessage("Ã‚Â§r                    Aoba Client " + net.aoba.AobaClient.VERSION);
				sendChatMessage("Ã‚Â§r                  Created By: coltonk9043   ");
				sendChatMessage("Ã‚Â§5              For help, please type .aoba help");
				sendChatMessage("Ã¢â€�â€�Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�â‚¬Ã¢â€�Ëœ");
				return;
			}
		}
		sendChatMessage("Invalid Command! Type .aoba help for a list of commands.");
	}

	public static void sendChatMessage(String message) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.inGameHud.getChatHud().addMessage(Text.of("Â§5[Aoba] Â§f" + message));
		//mc.inGameHud.getChatHud().addMessage(new MutableText("Ã‚Â§5[Aoba] Ã‚Â§f" + message));
	}
}
