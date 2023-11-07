package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.authlib.GameProfile;
import net.aoba.Aoba;
import net.aoba.gui.HudManager;
import net.aoba.misc.FakePlayerEntity;
import net.aoba.module.modules.movement.Fly;
import net.aoba.module.modules.movement.Freecam;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
	@Shadow
	private ClientPlayNetworkHandler networkHandler;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}
	
	@Inject(at = { @At("HEAD") }, method = "tick()V", cancellable = true)
	private void onPlayerTick(CallbackInfo ci) {
		if (Aoba.getInstance().moduleManager.freecam.getState()) {
			Freecam freecam = (Freecam) Aoba.getInstance().moduleManager.freecam;
			FakePlayerEntity fakePlayer = freecam.getFakePlayer();
			if(fakePlayer != null) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(fakePlayer.getX(), fakePlayer.getY(),
						fakePlayer.getZ(), fakePlayer.isOnGround()));
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(fakePlayer.getYaw(),
						fakePlayer.getPitch(), fakePlayer.isOnGround()));
			}
		}
	}
	
	@Inject(at = {@At("HEAD") }, method="sendMovementPackets()V", cancellable = true)
	private void sendMovementPackets(CallbackInfo ci) {
		if (Aoba.getInstance().moduleManager.freecam.getState()) {
			ci.cancel();
		}
	}
	
	@Inject (at = {@At("HEAD")}, method="setShowsDeathScreen(Z)V")
	private void onShowDeathScreen(boolean state, CallbackInfo ci) {
		HudManager hudManager = Aoba.getInstance().hudManager;
		if(state && hudManager.isClickGuiOpen()) {
			hudManager.setClickGuiOpen(false);
		}
	}
	
	@Override
	public boolean isSpectator() {
		return super.isSpectator() || Aoba.getInstance().moduleManager.freecam.getState();
	}
	
	@Override
	protected float getOffGroundSpeed()
	{
		float speed = super.getOffGroundSpeed();
		if(Aoba.getInstance().moduleManager.fly.getState()) {
			Fly fly = (Fly)Aoba.getInstance().moduleManager.fly;
			return (float)fly.getSpeed();
		}
		
		if(Aoba.getInstance().moduleManager.freecam.getState()) {
			Freecam freecam = (Freecam)Aoba.getInstance().moduleManager.freecam;
			return (float)freecam.getSpeed();
		}
		return speed;
	}

}
