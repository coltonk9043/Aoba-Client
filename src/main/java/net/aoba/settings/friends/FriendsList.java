package net.aoba.settings.friends;

import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.io.*;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.UUID;

public class FriendsList {
    private HashSet<Friend> friendsList;
    public static File configFolder;
    public static File friendsFile;

    public FriendsList() {
        friendsList = new HashSet<Friend>();
        configFolder = new File(MinecraftClient.getInstance().runDirectory + File.separator + "aoba");
        friendsFile = new File(configFolder + File.separator + "friends.xml");

        load();
    }

    public void save() {
        Properties props = new Properties();
        try {
            StringBuilder friendsBuilder = new StringBuilder();
            for (Friend friend : friendsList) {
                friendsBuilder.append(friend.getUsername());
                friendsBuilder.append(':');
                friendsBuilder.append(friend.getUUID().toString());
                friendsBuilder.append('\n');
            }

            props.loadFromXML(new FileInputStream(friendsFile));
            props.setProperty("friends", friendsBuilder.toString());
            props.storeToXML(new FileOutputStream(friendsFile), null);
        } catch (FileNotFoundException e) {
            LogUtils.getLogger().error("Friends List File not found.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
    public void load() {
        Properties props = new Properties();
        try {
            props.loadFromXML(new FileInputStream(friendsFile));

            // Since UUIDs are delimited using dashes, we can safely store the username and uuid in format: (username):(uuid).
            String value = props.getProperty("friends", null);
            String[] entries = value.split(",");

            for (String entry : entries) {
                String[] values = entry.split(":");
                if (values.length == 2) {
                    Friend friend = new Friend(values[0], UUID.fromString(values[1]));
                    friendsList.add(friend);
                }

            }
        } catch (FileNotFoundException e) {
            LogUtils.getLogger().error("Friends List File not found.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addFriend(PlayerEntity entity) {
        addFriend(entity.getName().getString(), entity.getUuid());
    }

    public void addFriend(String username, UUID uuid) {
        friendsList.add(new Friend(username, uuid));
    }

    public void removeFriend(PlayerEntity entity) {
        removeFriend(entity.getUuid());
    }

    public void removeFriend(UUID uuid) {
        for (Friend friend : friendsList) {
            if (friend.getUUID().equals(uuid)) {
                friendsList.remove(friend);
                break;
            }
        }
    }

    public boolean contains(PlayerEntity entity) {
        return contains(entity.getUuid());
    }

    public boolean contains(UUID uuid) {
        for (Friend friend : friendsList) {
            if (friend.getUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public HashSet<Friend> getFriends() {
        return friendsList;
    }
}
