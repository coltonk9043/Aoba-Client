package aoba.main.cmd;

import java.util.ArrayList;

import aoba.main.Aoba;
import aoba.main.cmd.commands.CmdAimbot;
import aoba.main.cmd.commands.CmdChestESP;
import aoba.main.cmd.commands.CmdEntityESP;
import aoba.main.cmd.commands.CmdFastBreak;
import aoba.main.cmd.commands.CmdFly;
import aoba.main.cmd.commands.CmdFullbright;
import aoba.main.cmd.commands.CmdHelp;
import aoba.main.cmd.commands.CmdItemESP;
import aoba.main.cmd.commands.CmdNoFall;
import aoba.main.cmd.commands.CmdNoSlowdown;
import aoba.main.cmd.commands.CmdNoclip;
import aoba.main.cmd.commands.CmdNuker;
import aoba.main.cmd.commands.CmdPOV;
import aoba.main.cmd.commands.CmdPlayerESP;
import aoba.main.cmd.commands.CmdSpam;
import aoba.main.cmd.commands.CmdSprint;
import aoba.main.cmd.commands.CmdStep;
import aoba.main.cmd.commands.CmdTP;
import aoba.main.cmd.commands.CmdTileBreaker;
import aoba.main.cmd.commands.CmdTimer;
import aoba.main.cmd.commands.CmdTracer;
import aoba.main.cmd.commands.CmdXRay;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandManager {
	public ArrayList<Command> commands = new ArrayList<Command>();
	
	int help = addCommand(new CmdHelp());
	int entityesp = addCommand(new CmdEntityESP());
	int chestesp = addCommand(new CmdChestESP());
	int fastbreak = addCommand(new CmdFastBreak());
	int fly = addCommand(new CmdFly());
	int fullbright = addCommand(new CmdFullbright());
	int itemesp = addCommand(new CmdItemESP());
	int noclip = addCommand(new CmdNoclip());
	int nofall = addCommand(new CmdNoFall());
	int noslowdown = addCommand(new CmdNoSlowdown());
	int nuker = addCommand(new CmdNuker());
	int pov = addCommand(new CmdPOV());
	int playeresp = addCommand(new CmdPlayerESP());
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
				// default syntax... so I just made a custom exception! ^-^
				sendChatMessage("--------------=---------------");
				sendChatMessage("               Aoba Client         ");
				sendChatMessage("        Created By: coltonk9043   ");
				sendChatMessage("               " + Aoba.VERSION);
				sendChatMessage("--------------=---------------");
				return;
			}
		}
		sendChatMessage("Invalid Command! Type .aoba help for a list of commands.");
	}

	public static void sendChatMessage(String message) {
		Minecraft mc = Minecraft.getInstance();
		mc.ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("§5[Aoba] §f" + message));
	}
}
