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
import net.aoba.AobaClient;
import net.aoba.event.events.PlayerHealthEvent;
import net.aoba.gui.GuiManager;
import net.aoba.mixin.interfaces.ICamera;
import net.aoba.module.modules.movement.Fly;
import net.aoba.module.modules.movement.Freecam;
import net.aoba.module.modules.movement.HighJump;
import net.aoba.module.modules.movement.Noclip;
import net.aoba.module.modules.movement.Step;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntityMixin {
	@Shadow
	private ClientPlayNetworkHandler networkHandler;
	
	@Inject (at = {@At("HEAD")}, method="setShowsDeathScreen(Z)V")
	private void onShowDeathScreen(boolean state, CallbackInfo ci) {
		GuiManager hudManager = Aoba.getInstance().hudManager;

		if(state && hudManager.isClickGuiOpen()) {
			hudManager.setClickGuiOpen(false);
		}
	}
	
	@Inject (at = {@At("HEAD")}, method="isCamera()Z", cancellable = true)
	private void onIsCamera(CallbackInfoReturnable<Boolean> cir) {
		Freecam freecam = (Freecam)Aoba.getInstance().moduleManager.freecam;
		if(freecam.getState()) {
			cir.setReturnValue(true);
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


	@Override
	protected void onGetOffGroundSpeed(CallbackInfoReturnable<Float> cir) {
		if(Aoba.getInstance().moduleManager.fly.getState()) {
			Fly fly = (Fly)Aoba.getInstance().moduleManager.fly;
			cir.setReturnValue((float)fly.getSpeed());
		}else if (Aoba.getInstance().moduleManager.noclip.getState()) {
			Noclip noclip = (Noclip)Aoba.getInstance().moduleManager.noclip;
			cir.setReturnValue(noclip.getSpeed());
		}
	}
	
	@Override
	public void onGetStepHeight(CallbackInfoReturnable<Float> cir) {
		Step stepHack = (Step) Aoba.getInstance().moduleManager.step;
		if(stepHack.getState()) {
			cir.setReturnValue(cir.getReturnValue());
		}
	}
	
	@Override
	public void onGetJumpVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
		AobaClient aoba = Aoba.getInstance();
		HighJump higherJump = (HighJump)aoba.moduleManager.higherjump;
		if(higherJump.getState()) {
			cir.setReturnValue(higherJump.getJumpHeightMultiplier());
		}
	}
	
	@Override
	public void onTickNewAi(CallbackInfo ci) {
		if(Aoba.getInstance().moduleManager.freecam.getState())
			ci.cancel();
	}
	
	@Override
	public void onChangeLookDirection(double cursorDeltaX,
			 double cursorDeltaY,
			 CallbackInfo ci) {
		if(Aoba.getInstance().moduleManager.freecam.getState()) {
			float f = (float)cursorDeltaY * 0.15f;
	        float g = (float)cursorDeltaX * 0.15f;

	        MinecraftClient mc = MinecraftClient.getInstance();
	        Camera camera = mc.gameRenderer.getCamera();
	        ICamera icamera = (ICamera)camera;
	        
	        float newYaw = camera.getYaw() + g;
	        float newPitch = Math.min(90, Math.max(camera.getPitch() + f, -90));
	        
	        icamera.setCameraRotation(newYaw, newPitch);
			ci.cancel();
		}
	}
}
