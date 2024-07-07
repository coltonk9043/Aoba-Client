package net.aoba.cmd.commands;

<<<<<<< Updated upstream
=======
import java.util.ArrayList;
>>>>>>> Stashed changes
import net.aoba.Aoba;
import net.aoba.cmd.Command;
import net.aoba.cmd.CommandManager;
import net.aoba.cmd.InvalidSyntaxException;
import net.aoba.settings.friends.Friend;
import net.aoba.settings.friends.FriendsList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class CmdFriends extends Command {

    public CmdFriends() {
        super("friends", "Allows the player to add and remove friends (Who will be excluded from many hacks)", "[add/remove/list] [value]");
    }

<<<<<<< Updated upstream
    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        FriendsList friendsList = Aoba.getInstance().friendsList;

        switch (parameters[0]) {
            case "add":
                String playerName = parameters[2].toLowerCase();
                try {

                } catch (InvalidIdentifierException e) {
                    CommandManager.sendChatMessage("Block " + parameters[2] + " could not be found.");
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
                return new String[]{"xray", "delete"};
            default:
                return new String[]{"add", "remove", "list"};
        }
    }
=======
	@Override
	public void runCommand(String[] parameters) throws InvalidSyntaxException {
		FriendsList friendsList = Aoba.getInstance().friendsList;
		MinecraftClient MC = MinecraftClient.getInstance();
		
		
		switch (parameters[0]) {
			case "add":
				{
					String playerName = parameters[1];
					try {	
						ServerPlayerEntity entity =  MC.getServer().getPlayerManager().getPlayer(playerName);
						if(entity != null) {
							Aoba.getInstance().friendsList.addFriend(entity.getName().getString(), entity.getUuid());
							CommandManager.sendChatMessage("Player " +  playerName + " was added to the friends list.");
						}else {
							CommandManager.sendChatMessage("Player " +  playerName + " could not be found.");
						}
					} catch (Exception e) {
						CommandManager.sendChatMessage("Player " +  playerName + " could not be added. Unknown error occured.");
						return;
					}
				}
				break;
			case "remove":
				{
					String playerName = parameters[1];
					ServerPlayerEntity entity =  MC.getServer().getPlayerManager().getPlayer(playerName);
					if(entity != null) {
						Aoba.getInstance().friendsList.removeFriend(entity.getUuid());
						CommandManager.sendChatMessage("Player " +  playerName + " was removed from the friends list.");
					}else {
						CommandManager.sendChatMessage("Player " +  playerName + " could not be found.");
					}
				}
				break;
			case "list":
				StringBuilder friends = new StringBuilder("Friends: ");
				for (Friend friend : friendsList.getFriends()) {
					friends.append(friend.getUsername()).append(", ");
				}
				friends.substring(0, friends.length() - 2);
				CommandManager.sendChatMessage(friends.toString());
				break;
			}
	}

	@Override
	public String[] getAutocorrect(String previousParameter) {
		switch (previousParameter) {
		case "add":
			return mc.getServer().getPlayerNames();
		case "remove":
			ArrayList<String> playerNames = new ArrayList<String>();
			for(Friend friend : Aoba.getInstance().friendsList.getFriends()) {
				playerNames.add(friend.getUsername());
			}
			return playerNames.toArray(new String[0]);
		default:
			return new String[] { "add", "remove", "list" };
		}
	}
>>>>>>> Stashed changes
}
