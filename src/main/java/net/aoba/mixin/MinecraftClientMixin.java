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

import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.session.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin{

	@Shadow
	private int itemUseCooldown;
	@Shadow
	@Final
	private Session session;
	
	@Shadow
	@Final
	private Mouse mouse;
	
	@Shadow
	public ClientWorld world;

	private Session aobaSession;

	@Inject(at = @At("HEAD"), method = "onFinishedLoading(Lnet/minecraft/client/MinecraftClient$LoadingContext;)V")
	private void onfinishedloading(CallbackInfo info) {
		Aoba.getInstance().loadAssets();
	}
	
	// TODO: this was moved to the FontManager class.
	//@Inject(at = @At("TAIL"), method = "initFont(Z)V")
	//private void onInitFont(boolean forcesUnicode, CallbackInfo info) {
	//	Aoba.getInstance().loadAssets();
	//}
	
	@Inject(at = @At("TAIL"), method = "tick()V")
	public void tick(CallbackInfo info) {
		if (this.world != null) {
			TickEvent updateEvent = new TickEvent();
			Aoba.getInstance().eventManager.Fire(updateEvent);
			
			Aoba.getInstance().update();
		}
	}

	@Inject(at = {@At("HEAD")}, method = {"getSession()Lnet/minecraft/client/session/Session;"}, cancellable = true)
		private void onGetSession(CallbackInfoReturnable<Session> cir)
		{
			if(aobaSession == null) return;
			cir.setReturnValue(aobaSession);
		}
	
	@Redirect(at = @At(value = "FIELD",target = "Lnet/minecraft/client/MinecraftClient;session:Lnet/minecraft/client/session/Session;",opcode = Opcodes.GETFIELD,ordinal = 0),method = {"getSession()Lnet/minecraft/client/session/Session;"})
	private Session getSessionForSessionProperties(MinecraftClient mc)
	{
		if(aobaSession != null)return aobaSession;
		return session;
	}
	
	@Inject(at = {@At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 0)}, method = {"doAttack()Z"}, cancellable = true)
	private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
		//double mouseX = Math.ceil(mouse.getX());
		//double mouseY = Math.ceil(mouse.getY());
		
		//System.out.println("DOuble Click?");
		//MouseLeftClickEvent event = new MouseLeftClickEvent(mouseX, mouseY);
		
		//Aoba.getInstance().eventManager.Fire(event);
		
		//if(event.IsCancelled()) {
		//	cir.setReturnValue(false);
		//	cir.cancel();
		//}
	}
	
	@Inject(at = {@At(value = "HEAD")}, method = {"close()V"})
	private void onClose(CallbackInfo ci) {
		try {
			Aoba.getInstance().endClient();
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	@Inject(at = {@At(value="HEAD")}, method = {"openGameMenu(Z)V"})
	private void onOpenPauseMenu(boolean pause, CallbackInfo ci) {
		Aoba.getInstance().hudManager.setClickGuiOpen(false);
	}
}
