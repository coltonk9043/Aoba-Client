/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.friends;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import net.aoba.utils.types.ObservableHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import com.mojang.logging.LogUtils;

public class FriendsList {
	private final File friendsFile;
	private final ObservableHashSet<Friend> friendsList = new ObservableHashSet<Friend>();

	public FriendsList() {
		File configFolder = new File(Minecraft.getInstance().gameDirectory, "aoba");
		friendsFile = new File(configFolder, "friends.xml");
		load();
	}

	public void save() {
		try (FileOutputStream fos = new FileOutputStream(friendsFile)) {
			Properties props = new Properties();
			StringBuilder friendsBuilder = new StringBuilder();
			for (Friend friend : friendsList) {
				friendsBuilder.append(friend.getUsername()).append(':').append(friend.getUUID()).append('\n');
			}
			props.setProperty("friends", friendsBuilder.toString());
			props.storeToXML(fos, null);
			LogUtils.getLogger().info("Saved friends list to " + friendsFile);
		} catch (IOException e) {
			LogUtils.getLogger().error("Failed to save friends list", e);
		}
	}

	public void load() {
		try (FileInputStream fis = new FileInputStream(friendsFile)) {
			Properties props = new Properties();
			props.loadFromXML(fis);
			String friends = props.getProperty("friends");
			if (friends != null) {
				for (String entry : friends.split("\n")) {
					String[] values = entry.split(":");
					if (values.length == 2) {
						friendsList.add(new Friend(values[0], UUID.fromString(values[1])));
					}
				}
			}
		} catch (IOException e) {
			LogUtils.getLogger().error("Failed to load friends list", e);
		}
	}

	public void addFriend(Player entity) {
		addFriend(entity.getName().getString(), entity.getUUID());
	}

	public void addFriend(String username, UUID uuid) {
		friendsList.add(new Friend(username, uuid));
	}

	public void removeFriend(Friend friend) {
		friendsList.remove(friend);
	}
	
	public void removeFriend(Player player) {
		removeFriend(player.getUUID());
	}
	
	public void removeFriend(UUID uuid) {
		ArrayList<Friend> toRemove = new ArrayList<Friend>();
		for(Friend friend : friendsList) {
			if(uuid.equals(friend.getUUID())) {
				toRemove.add(friend);
			}
		}
		for(Friend friend : toRemove) {
			friendsList.remove(friend);
		}
	}
	
	public boolean contains(Player entity) {
		return contains(entity.getUUID());
	}

	public boolean contains(UUID uuid) {
		return friendsList.stream().anyMatch(friend -> friend.getUUID().equals(uuid));
	}

	public ObservableHashSet<Friend> getFriends() {
		return friendsList;
	}
}
