package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.managers.CommandManager;
import net.aoba.event.events.GameLeftEvent;
import net.aoba.event.events.SendMessageEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
	@Shadow
	private ClientWorld world;

	@Shadow
	public abstract void sendChatMessage(String content);

	@Unique
	private boolean ignoreChatMessage;

	@Unique
	private boolean worldNotNull;

	protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection,
			ClientConnectionState connectionState) {
		super(client, connection, connectionState);
	}

	@Inject(method = "onGameJoin", at = @At("TAIL"))
	private void onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info) {
		if (worldNotNull) {
			GameLeftEvent gameLeftEvent = new GameLeftEvent();

			Aoba.getInstance().eventManager.Fire(gameLeftEvent);
		}

		// At some point fire a game joined event here
	}

	@Inject(method = "onEnterReconfiguration", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER))
	private void onEnterReconfiguration(EnterReconfigurationS2CPacket packet, CallbackInfo info) {
		GameLeftEvent gameLeftEvent = new GameLeftEvent();

		Aoba.getInstance().eventManager.Fire(gameLeftEvent);
	}

	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	private void onSendChatMessage(String message, CallbackInfo ci) {
		if (ignoreChatMessage)
			return;

		if (!message.startsWith(CommandManager.PREFIX.getValue())) {
			SendMessageEvent sendMessageEvent = new SendMessageEvent(message);
			Aoba.getInstance().eventManager.Fire(sendMessageEvent);

			if (!sendMessageEvent.isCancelled()) {
				ignoreChatMessage = true;
				sendChatMessage(sendMessageEvent.getMessage());
				ignoreChatMessage = false;
			}
			ci.cancel();
        }
	}
}