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
import java.util.HashSet;
import java.util.Properties;
import java.util.UUID;

import com.mojang.logging.LogUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class FriendsList {
	private final File friendsFile;
	private final HashSet<Friend> friendsList = new HashSet<>();

	public FriendsList() {
		File configFolder = new File(MinecraftClient.getInstance().runDirectory, "aoba");
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
		friendsList.removeIf(friend -> friend.getUUID().equals(uuid));
	}

	public boolean contains(PlayerEntity entity) {
		return contains(entity.getUuid());
	}

	public boolean contains(UUID uuid) {
		return friendsList.stream().anyMatch(friend -> friend.getUUID().equals(uuid));
	}

	public HashSet<Friend> getFriends() {
		return friendsList;
	}
}
