package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.movement.Freecam;
import net.aoba.module.modules.render.NoRender;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;

@Mixin(Camera.class)
public class CameraMixin {
	@Shadow
	private boolean initialized;

	@Shadow
	private Entity entity;

	@Shadow
	private Level level;

	@Inject(at = {
			@At("HEAD") }, method = "setEntity(Lnet/minecraft/world/entity/Entity;)V", cancellable = true)
	private void onSetEntity(Entity entity, CallbackInfo ci) {
		AobaClient aoba = Aoba.getInstance();

		if (aoba != null && aoba.moduleManager.freecam.state.getValue() && this.entity != null) {
			ci.cancel();
		}
	}

	@Inject(at = { @At("HEAD") }, method = "alignWithEntity(F)V", cancellable = true)
	private void onAlignWithEntity(float partialTicks, CallbackInfo ci) {
		AobaClient aoba = Aoba.getInstance();

		if (aoba != null && aoba.moduleManager.freecam.state.getValue() && this.entity != null) {
			ci.cancel();
		}
	}

	@Inject(at = {
			@At("HEAD") }, method = "getFluidInCamera()Lnet/minecraft/world/level/material/FogType;", cancellable = true)
	private void onGetSubmersionType(CallbackInfoReturnable<FogType> cir) {
		AobaClient aoba = Aoba.getInstance();
		if (aoba == null)
			return;

		Freecam freecam = aoba.moduleManager.freecam;
		NoRender norender = aoba.moduleManager.norender;

		if (freecam.state.getValue() || (norender.state.getValue() && norender.getNoLiquidOverlay())) {
			cir.setReturnValue(FogType.NONE);
		}
	}
}
