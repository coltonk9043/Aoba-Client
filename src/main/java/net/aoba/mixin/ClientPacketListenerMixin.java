package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.managers.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket;
import net.aoba.event.events.GameLeftEvent;
import net.aoba.event.events.SendMessageEvent;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
	@Shadow
	private ClientLevel level;

	@Shadow
	public abstract void sendChat(String content);

	@Unique
	private boolean ignoreChatMessage;

	@Unique
	private boolean worldNotNull;

	protected ClientPacketListenerMixin(Minecraft client, Connection connection,
			CommonListenerCookie connectionState) {
		super(client, connection, connectionState);
	}

	@Inject(method = "handleLogin", at = @At("TAIL"))
	private void onGameJoinTail(ClientboundLoginPacket packet, CallbackInfo info) {
		if (worldNotNull) {
			GameLeftEvent gameLeftEvent = new GameLeftEvent();

			Aoba.getInstance().eventManager.Fire(gameLeftEvent);
		}
	}

	@Inject(method = "handleConfigurationStart", at = @At("HEAD"))
	private void onEnterReconfiguration(ClientboundStartConfigurationPacket packet, CallbackInfo info) {
		GameLeftEvent gameLeftEvent = new GameLeftEvent();
		Aoba.getInstance().eventManager.Fire(gameLeftEvent);
	}

	@Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
	private void onSendChatMessage(String message, CallbackInfo ci) {
		if (ignoreChatMessage)
			return;

		if (!message.startsWith(CommandManager.PREFIX.getValue())) {
			SendMessageEvent sendMessageEvent = new SendMessageEvent(message);
			Aoba.getInstance().eventManager.Fire(sendMessageEvent);

			if (!sendMessageEvent.isCancelled()) {
				ignoreChatMessage = true;
				sendChat(sendMessageEvent.getMessage());
				ignoreChatMessage = false;
			}
			ci.cancel();
        }
	}
}