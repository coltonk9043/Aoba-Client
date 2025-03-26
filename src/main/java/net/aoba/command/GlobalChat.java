/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mojang.logging.LogUtils;

import net.aoba.managers.SettingManager;
import net.aoba.settings.types.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;

public class GlobalChat {

	public enum ChatType {
		Minecraft, Global
	}

	public static ChatType chatType = ChatType.Minecraft;

	public final List<ChatHudLine.Visible> messages = Lists.newArrayList();

	private final Gson gson;
	private Socket socket;
	private Thread chatSocketListenerThread;
	private PrintWriter out;
	private BufferedReader in;
	private boolean started = false;

	private final BooleanSetting enabled = BooleanSetting.builder().id("global_chat_enabled")
			.description("Whether or not global chat is enabled or disabled.").defaultValue(true).build();

	public GlobalChat() {
		gson = new Gson();
		SettingManager.registerSetting(enabled);
	}

	private void Send(String json) {
		if (out != null) {
			out.print(json);
			out.flush();
		}
	}

	public void SendMessage(String message) {
		Send(gson.toJson(new MessageAction(message, null)));
	}

	private void SendChatMessage(String message) {
		MinecraftClient MC = MinecraftClient.getInstance();
		if (MC != null && MC.inGameHud != null) {
			messages.add(0, new ChatHudLine.Visible(MC.inGameHud.getTicks(), Text.of(message).asOrderedText(),
					MessageIndicator.system(), false));

		}
	}

	public void StartListener() {
		if (started) {
			LogUtils.getLogger().info("Socket listener already started.");
			return;
		}

		try {
			started = true;

			chatSocketListenerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// Gotta love AWS!
						socket = new Socket("chat.aobaclient.com", 42069);
						out = new PrintWriter(socket.getOutputStream(), false);
						in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

						// Send the connection action to the server.
						Send(gson.toJson(new ConnectAction(MinecraftClient.getInstance().getSession().getUsername())));

						String json = in.readLine();
						while (json != null && MinecraftClient.getInstance() != null) {
							MessageResponse response = new Gson().fromJson(json, MessageResponse.class);
							if (response != null) {
								String user = response.getUser();
								String chatMessage = response.getMessage();
								if (user != null && chatMessage != null) {
									SendChatMessage(String.format("<%s> %s", user, chatMessage));
								}
							}
							json = in.readLine();
						}

						out.close();
						in.close();
						socket.close();
					} catch (UnknownHostException e) {
						LogUtils.getLogger().error("Cannot connect to chat server");
					} catch (ConnectException e) {
						LogUtils.getLogger().error("Error connecting to chat server: ", e);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			chatSocketListenerThread.start();
		} catch (Exception e) {
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
class GenericAction {
	public String action;

	public GenericAction(String action) {
		this.action = action;
	}
}

class ConnectAction extends GenericAction {
	public String username;

	public ConnectAction(String username) {
		super("connect");
		this.username = username;
	}
}

class MessageAction extends GenericAction {
	public String message;
	public String to;

	public MessageAction(String message, String to) {
		super("message");
		this.message = message;
		this.to = to;
	}
}

/**
 * Server Response
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
