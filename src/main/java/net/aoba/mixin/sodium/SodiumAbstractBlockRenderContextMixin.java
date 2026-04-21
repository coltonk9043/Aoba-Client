package net.aoba.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.render.XRay;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = AbstractBlockRenderContext.class, remap = false)
public abstract class SodiumAbstractBlockRenderContextMixin {

	@Shadow
	protected BlockState state;

	@Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
	private void onShouldDrawSide(Direction face, CallbackInfoReturnable<Boolean> cir) {
		AobaClient aoba = Aoba.getInstance();
		if (aoba == null) return;
		XRay xray = aoba.moduleManager.xray;
		if (xray.state.getValue() && state != null && xray.isXRayBlock(state.getBlock())) {
			cir.setReturnValue(true);
		}
	}
}
