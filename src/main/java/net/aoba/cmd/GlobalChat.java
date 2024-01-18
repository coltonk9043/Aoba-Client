package net.aoba.cmd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.aoba.Aoba;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GlobalChat {

	private Gson gson;
	private Socket socket;
	private Thread chatSocketListenerThread;
	private PrintWriter out;
	private BufferedReader in;
	private boolean started = false;
	
	private BooleanSetting enabled;
	
	public GlobalChat() {
		gson = new Gson();
		enabled = new BooleanSetting("global_chat_enabled", "Whether or not global chat is enabled or disabled.", true);
		SettingManager.register_setting(this.enabled, Aoba.getInstance().settingManager.modules_category);
	}
	
	private void Send(String json) {
		out.print(json);
		out.flush();
	}

	public void SendMessage(String message) {
		Send(gson.toJson(new MessageAction(message, null)));
	}
	
	private void SendChatMessage(String message) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if(mc.inGameHud != null) {
			mc.inGameHud.getChatHud().addMessage(Text.of(Formatting.DARK_PURPLE + "[" + Formatting.LIGHT_PURPLE + "GLOBAL" + Formatting.DARK_PURPLE +  "] " + Formatting.RESET + message));
		}
	}
	
	
	public void StartListener() {
		if(started) {
			System.out.println("Socket listener already started.");
			return;
		}
		
		try {
			started = true;
			
			// Gotta love AWS!
			socket = new Socket("18.221.222.43", 80);
			out = new PrintWriter(socket.getOutputStream(), false);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			chatSocketListenerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// Send the connection action to the server.
						Send(gson.toJson(new ConnectAction(MinecraftClient.getInstance().getSession().getUsername())));
						
						String json = in.readLine();
						while(json != null && MinecraftClient.getInstance() != null) {
							MessageResponse response = new Gson().fromJson(json, MessageResponse.class);
							if(response != null) {
								String user = response.getUser();
								String chatMessage = response.getMessage();
								if(user != null && chatMessage != null) {
									SendChatMessage(String.format("<%s> %s", user, chatMessage));
								}
							}
							json = in.readLine();
						}
						
						out.close();
						in.close();
						socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});  
			chatSocketListenerThread.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void RemoveListener() {
		Send(gson.toJson(new GenericAction("disconnect")));
	}
}

/**
 * Defines classes that will be parsed to Json and passed to the chat server.
 */
class GenericAction{
	public String action;
	public GenericAction(String action) { this.action = action; }
}

class ConnectAction extends GenericAction {
	public String username;
	public ConnectAction(String username) { 
		super("connect"); 
		this.username = username;
	}
}

class MessageAction extends GenericAction{
	public String message;
	public String to;
	public MessageAction(String message, String to) { 
		super("message"); 
		this.message = message;
		this.to = to;
	}
}

/**
 *  Server Response
 */
class MessageResponse {
	private String message;
	private String user;
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
}



