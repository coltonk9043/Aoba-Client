package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.misc.FakePlayerEntity;
import net.aoba.module.modules.movement.Freecam;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity{
	@Shadow
	private ClientPlayNetworkHandler networkHandler;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(at = { @At("HEAD") }, method = "sendChatMessage(Ljava/lang/String;)V", cancellable = true)
	private void onSendChatMessage(String message, CallbackInfo ci) {
		if (message.startsWith(AobaClient.PREFIX)) {
			Aoba.getInstance().cm.command(message.split(" "));
			ci.cancel();
		}
	}
	
	@Inject(at = { @At("HEAD") }, method = "tick()V", cancellable = true)
	private void onPlayerTick(CallbackInfo ci) {
		if (Aoba.getInstance().mm.freecam.getState()) {
			Freecam freecam = (Freecam) Aoba.getInstance().mm.freecam;
			FakePlayerEntity fakePlayer = freecam.getFakePlayer();
			if(fakePlayer != null) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(fakePlayer.getX(), fakePlayer.getY(),
						fakePlayer.getZ(), fakePlayer.isOnGround()));
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(fakePlayer.getYaw(),
						fakePlayer.getPitch(), fakePlayer.isOnGround()));
			}
			ci.cancel();
		}
	}
}
