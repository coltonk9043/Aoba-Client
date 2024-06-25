package net.aoba.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class FriendsList {

	private HashSet<UUID> friendsList;
	public static File configFolder;
	public static File friendsFile;
	
	public FriendsList() {
		friendsList = new HashSet<UUID>();
		configFolder = new File(MinecraftClient.getInstance().runDirectory + File.separator + "aoba");
		friendsFile = new File(configFolder + File.separator +  "friends.xml");
		
		load();
	}
	
	public void save() {
		Properties props = new Properties();
		try {
			props.loadFromXML(new FileInputStream(friendsFile));
			props.setProperty("friends", null);
			props.storeToXML(new FileOutputStream(friendsFile), null);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		Properties props = new Properties();
		try {
			props.loadFromXML(new FileInputStream(friendsFile));
			
			String value = props.getProperty("friends", null);
			String[] UUIDs = value.split(",");
			
			for(String uuid : UUIDs) {
				friendsList.add(UUID.fromString(uuid));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addFriend(PlayerEntity entity) {
		addFriend(entity.getUuid());
	}
	
	public void addFriend(UUID uuid) {
		friendsList.add(uuid);
	}
	
	public void removeFriend(PlayerEntity entity) {
		removeFriend(entity.getUuid());
	}
	
	public void removeFriend(UUID uuid) {
		friendsList.remove(uuid);
	}
	
	public boolean contains(PlayerEntity entity) {
		return contains(entity.getUuid());
	}
	
	public boolean contains(UUID uuid) {
		return friendsList.contains(uuid);
	}
	
	public HashSet<UUID> getFriends(){
		return friendsList;
	}
}
