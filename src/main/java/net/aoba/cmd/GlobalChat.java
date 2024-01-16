package net.aoba.cmd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.MinecraftClient;

public class GlobalChat {

	private Gson gson;
	private Socket socket;
	private Thread chatSocketListenerThread;
	private PrintWriter out;
	private BufferedReader in;
	private boolean started = false;
	
	public GlobalChat() {
		gson = new Gson();
	}
	
	private void Send(String json) {
		out.print(json);
		out.flush();
	}

	public void SendMessage(String message) {
		Send(gson.toJson(new MessageAction(message, null)));
	}
	
	public void StartListener() {
		if(started) {
			System.out.println("Socket listener already started.");
			return;
		}
		
		try {
			started = true;
			socket = new Socket("18.119.121.174", 80);
			out = new PrintWriter(socket.getOutputStream(), false);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			chatSocketListenerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// Send the connection action to the server.
						Send(gson.toJson(new ConnectAction(MinecraftClient.getInstance().getSession().getUsername())));
						
						String json = in.readLine();
						while(json != null) {
							Map<String, String> map = new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() {}.getType());
							if(map.containsKey("message")) {
								String message = map.get("message");
								CommandManager.sendChatMessage(message);
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


