package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.module.modules.render.NoRender;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
	@Inject(method = "renderFireOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
	private static void onRenderFireOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			CallbackInfo info) {
		NoRender norender = (NoRender) Aoba.getInstance().moduleManager.norender;

		if (norender.state.getValue() && norender.getNoFireOverlay())
			info.cancel();
	}
}
