/*
* Aoba Hacked Client
* Copyright (C) 2019-2024 coltonk9043
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import net.aoba.Aoba;
import net.aoba.cmd.CommandManager;
import net.aoba.cmd.GlobalChat;
import net.aoba.cmd.GlobalChat.ChatType;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen{
	protected ChatScreenMixin(Text title) {
		super(title);
	}

	@Shadow
	protected TextFieldWidget chatField;
	
	protected ButtonWidget serverChatButton;
	protected ButtonWidget globalChatButton;
	
	
	@Inject(at = { @At("TAIL") }, method = {"init()V" }, cancellable = true)
	public void onInit(CallbackInfo ci) {
		serverChatButton = ButtonWidget.builder(Text.of("Server Chat"), s -> { switchToServer(); }).dimensions(chatField.getX(), chatField.getY() - chatField.getHeight() - 10, 70, 15).build();
		globalChatButton = ButtonWidget.builder(Text.of("Global Chat"), s -> { switchToGlobal(); }).dimensions(chatField.getX() + 80, chatField.getY() - chatField.getHeight() - 10, 70, 15).build();
		this.addDrawableChild(serverChatButton);
		this.addDrawableChild(globalChatButton);
		
		serverChatButton.active = !(GlobalChat.chatType == ChatType.Minecraft);
		globalChatButton.active = !(GlobalChat.chatType == ChatType.Global);
	}
	
	@Inject(at = {
			@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addToMessageHistory(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER) }, method = "sendMessage(Ljava/lang/String;Z)Z", cancellable = true)
	public void onSendMessage(String message, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
		if (message.startsWith(CommandManager.PREFIX.getValue())) {
			Aoba.getInstance().commandManager.command(message.split(" "));
			cir.setReturnValue(true);
		}else if (GlobalChat.chatType == ChatType.Global) {
			Aoba.getInstance().globalChat.SendMessage(message);
			cir.setReturnValue(true);
		}
	}
	
	
	// TODO: For some dumb reason, the chat field unfocused when the chat window is switched. 
	// Tried a few possible solutions (focus, selectedtext, etc..) but none seem to work.
	private void switchToGlobal() {
		GlobalChat.chatType = GlobalChat.ChatType.Global;
		
		if(globalChatButton != null) {
			globalChatButton.active = false;
			serverChatButton.active = true;
		}
	}
	
	private void switchToServer() {
		GlobalChat.chatType = GlobalChat.ChatType.Minecraft;

		if(serverChatButton != null) {
			globalChatButton.active = true;
			serverChatButton.active = false;
		}
	}
}
