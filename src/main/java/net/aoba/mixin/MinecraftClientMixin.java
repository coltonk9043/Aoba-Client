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

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.TickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.session.Session;
import net.minecraft.client.world.ClientWorld;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

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

	@Shadow
	public ClientPlayerEntity player;

	private Session aobaSession;

	@Shadow
	public abstract boolean isWindowFocused();

	@Shadow
	@Final
	public GameOptions options;

	@Inject(at = @At("HEAD"), method = "onFinishedLoading(Lnet/minecraft/client/MinecraftClient$LoadingContext;)V")
	private void onfinishedloading(CallbackInfo info) {
		Aoba.getInstance().loadAssets();
	}

	@Inject(at = @At("HEAD"), method = "tick()V")
	public void onPreTick(CallbackInfo info) {
		if (world != null && player != null) {
			TickEvent.Pre updateEvent = new TickEvent.Pre();
			Aoba.getInstance().eventManager.Fire(updateEvent);
		}
	}

	@Inject(at = @At("TAIL"), method = "tick()V")
	public void onPostTick(CallbackInfo info) {
		if (world != null && player != null) {
			TickEvent.Post updateEvent = new TickEvent.Post();
			Aoba.getInstance().eventManager.Fire(updateEvent);
		}
	}

	@Inject(at = { @At("HEAD") }, method = { "getSession()Lnet/minecraft/client/session/Session;" }, cancellable = true)
	private void onGetSession(CallbackInfoReturnable<Session> cir) {
		if (aobaSession == null)
			return;
		cir.setReturnValue(aobaSession);
	}

	@Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;session:Lnet/minecraft/client/session/Session;", opcode = Opcodes.GETFIELD, ordinal = 0), method = {
			"getSession()Lnet/minecraft/client/session/Session;" })
	private Session getSessionForSessionProperties(MinecraftClient mc) {
		if (aobaSession != null)
			return aobaSession;
		return session;
	}

	@Inject(at = { @At(value = "HEAD") }, method = { "close()V" })
	private void onClose(CallbackInfo ci) {
		try {
			Aoba.getInstance().endClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Inject(at = { @At(value = "HEAD") }, method = { "openGameMenu(Z)V" })
	private void onOpenPauseMenu(boolean pause, CallbackInfo ci) {
		AobaClient aoba = Aoba.getInstance();

		if (aoba.guiManager != null) {
			Aoba.getInstance().guiManager.setClickGuiOpen(false);
		}
	}

	// TODO: InactivityFrameLimiter class... i guess.. :/
	/*
	 * @Inject(method = "getCurrentFps", at = @At("HEAD"), cancellable = true)
	 * private void onGetCurrentFps(CallbackInfoReturnable<Integer> info) { if
	 * (Aoba.getInstance().moduleManager != null) { FocusFps focusfps = (FocusFps)
	 * Aoba.getInstance().moduleManager.focusfps; if (focusfps.state.getValue() &&
	 * !isWindowFocused()) {
	 * info.setReturnValue(Math.min(focusfps.getFps().intValue(),
	 * this.options.getMaxFps().getValue())); } } }
	 */
}
