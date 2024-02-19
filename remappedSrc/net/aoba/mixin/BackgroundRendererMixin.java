package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.aoba.Aoba;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.entity.Entity;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin{
	@Inject(at = {@At("HEAD")},
			method = {
				"getFogModifier(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;"},
			cancellable = true)
		private static void onGetFogModifier(Entity entity, float tickDelta,
			CallbackInfoReturnable<?> cir)
		{
			if(Aoba.getInstance().moduleManager.nooverlay.getState())
				cir.setReturnValue(null);
		}
}
