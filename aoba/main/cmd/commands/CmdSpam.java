package aoba.main.cmd.commands;

import aoba.main.cmd.Command;
import aoba.main.cmd.CommandManager;

public class CmdSpam extends Command {

	public CmdSpam() {
		this.command = "spam";
		this.description = "Spams the chat with a certain message";
	}

	@Override
	public void command(String[] parameters) {
		if (parameters.length > 1) {
			String message = "";
			for(int msg = 1; msg < parameters.length; msg++) {
				message = message + parameters[msg] + " ";
			}
			for(int i = 0; i < Integer.parseInt(parameters[0]); i++) {
				mc.player.sendChatMessage(message);
			}
		}else {
			CommandManager.sendChatMessage("Invalid Usage! Use format '.aoba spam [Times] [Message]'");
		}
	}
}
