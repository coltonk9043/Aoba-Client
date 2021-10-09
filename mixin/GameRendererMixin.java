package net.aoba.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(
			at = {@At(value = "FIELD",
				target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
				opcode = Opcodes.GETFIELD,
				ordinal = 0)},
			method = {
				"renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V"})
		private void onRenderWorld(float partialTicks, long finishTimeNano,
			MatrixStack matrixStack, CallbackInfo ci)
		{
			Aoba.getInstance().mm.render(matrixStack, partialTicks);
		}
}
