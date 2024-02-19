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
	
	protected ButtonWidget button;
	
	@Inject(at = { @At("TAIL") }, method = {"init()V" }, cancellable = true)
	public void onInit(CallbackInfo ci) {
		this.addDrawableChild(ButtonWidget.builder(Text.of("Server Chat"), s -> GlobalChat.chatType = GlobalChat.ChatType.Minecraft).dimensions(chatField.getX(), chatField.getY() - chatField.getHeight() - 10, 70, 15).build());
		this.addDrawableChild(ButtonWidget.builder(Text.of("Global Chat"), s -> GlobalChat.chatType = GlobalChat.ChatType.Global).dimensions(chatField.getX() + 80, chatField.getY() - chatField.getHeight() - 10, 70, 15).build());
	}
	
	@Inject(at = {
			@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addToMessageHistory(Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.AFTER) }, method = "sendMessage(Ljava/lang/String;Z)Z", cancellable = true)
	public void onSendMessage(String message, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
		if (message.startsWith(CommandManager.PREFIX.getValue())) {
			Aoba.getInstance().commandManager.command(message.split(" "));
			cir.setReturnValue(true);
		}else if (message.startsWith(".global")) {
			Aoba.getInstance().globalChat.SendMessage(message.substring(8, message.length()));
			cir.setReturnValue(true);
		}
	}
}
