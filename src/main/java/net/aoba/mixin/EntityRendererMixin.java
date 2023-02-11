package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import net.aoba.Aoba;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

//	@Inject(at = { @At("HEAD") }, method = {
//			"renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V" }, cancellable = true)
//	private void onRenderLabelIfPresent(T entity, float yaw, float tickDelta, MatrixStack matrices,
//			VertexConsumerProvider vertexConsumers, int light) {
//		if (Aoba.getInstance().mm.nametags.getState()) {
//			matrices.scale(2, 2, 2);
//		}
//	}
}
