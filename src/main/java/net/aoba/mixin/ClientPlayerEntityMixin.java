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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.aoba.Aoba;
import net.aoba.event.events.PlayerHealthEvent;
import net.aoba.gui.GuiManager;
import net.aoba.misc.FakePlayerEntity;
import net.aoba.module.modules.movement.Freecam;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntityMixin {
	@Shadow
	private ClientPlayNetworkHandler networkHandler;
	
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
		GuiManager hudManager = Aoba.getInstance().hudManager;

		if(state && hudManager.isClickGuiOpen()) {
			hudManager.setClickGuiOpen(false);
		}
	}

	
	@Override
	public void onIsSpectator(CallbackInfoReturnable<Boolean> cir) {
		if(Aoba.getInstance().moduleManager.freecam.getState()) {
			cir.setReturnValue(true);
		}
	}
	
	@Override
	public void onSetHealth(float health, CallbackInfo ci) {
		PlayerHealthEvent event = new PlayerHealthEvent(null, health);
		Aoba.getInstance().eventManager.Fire(event);
	}


//	@Override
//	protected float getOffGroundSpeed() {
//		float speed = super.getOffGroundSpeed();
//		if(Aoba.getInstance().moduleManager.fly.getState()) {
//			Fly fly = (Fly)Aoba.getInstance().moduleManager.fly;
//			return (float)fly.getSpeed();
//		}
//
//		if(Aoba.getInstance().moduleManager.freecam.getState()) {
//			Freecam freecam = (Freecam)Aoba.getInstance().moduleManager.freecam;
//			return (float)freecam.getSpeed();
//		}
//		return speed;
//	}
}
