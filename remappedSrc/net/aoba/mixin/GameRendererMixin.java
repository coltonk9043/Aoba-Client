package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.aoba.Aoba;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(at = {@At("HEAD")}, method = { "bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"}, cancellable = true)
	private void onBobViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo ci) {
		if (Aoba.getInstance().moduleManager.nooverlay.getState()) {
			ci.cancel();
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0), method = {
			"renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V" })
	private float nauseaLerp(float delta, float first, float second) {
		if (Aoba.getInstance().moduleManager.nooverlay.getState())
			return 0;
		
		return MathHelper.lerp(delta, first, second);
	}
}
