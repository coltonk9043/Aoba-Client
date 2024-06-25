package net.aoba.cmd.commands;

import java.util.UUID;

import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.cmd.InvalidSyntaxException;
import net.aoba.settings.FriendsList;
import net.minecraft.util.InvalidIdentifierException;

public class CmdFriends extends Command {

	public CmdFriends() {
		super("friends", "Allows the player to add and remove friends (Who will be excluded from many hacks)", "[add/remove/list] [value]");
	}

	@Override
	public void runCommand(String[] parameters) throws InvalidSyntaxException {
		FriendsList friendsList = Aoba.getInstance().friendsList;
		
		switch (parameters[0]) {
			case "add":
				String playerName = parameters[2].toLowerCase();
				try {	
					
				} catch (InvalidIdentifierException e) {
					CommandManager.sendChatMessage("Block " +  parameters[2] + " could not be found.");
					return;
				}
				break;
			case "remove":
				
				break;
			case "list":
				String friends = "";
				for (UUID uuid : friendsList.getFriends()) {
					friends += uuid.toString() + ", ";
				}
				friends = friends.substring(0, friends.length() - 2);
				CommandManager.sendChatMessage("Friends: " + friends);
				break;
			}
	}

	@Override
	public String[] getAutocorrect(String previousParameter) {
		switch (previousParameter) {
		case "add":
			return mc.getServer().getPlayerNames();
		case "remove":
			return new String[] { "xray", "delete" };
		default:
			return new String[] { "add", "remove", "list" };
		}
	}
}
