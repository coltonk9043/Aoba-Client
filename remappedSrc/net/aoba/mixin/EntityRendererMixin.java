package net.aoba.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.combat.Nametags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
	@Shadow
	@Final
	protected EntityRenderDispatcher dispatcher;
	
	@Inject(at = @At(value = "HEAD"), 
			method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", 
			cancellable=true)
	protected void onRenderLabelIfPresent(T entity,
			 Text text,
			 MatrixStack matrices,
			 VertexConsumerProvider vertexConsumers,
			 int light, CallbackInfo ci) {
		CustomRenderLabel(entity, text, matrices, vertexConsumers, light);
			ci.cancel();
	}
	
	@Shadow
	public TextRenderer getTextRenderer()
	{
		return null;
	}
	
	/**
	 * Custom Label Render that will allow us to Render what we'd like in the future. 
	 * TODO: If this can be replaced by an INVOKE @ matrixStack.scale(), I would love to do that.
	 * @param entity Entity being currently rendered.
	 * @param text The text to render.
	 * @param matrices The MatrixStack.
	 * @param vertexConsumers Vertex Consumers
	 * @param light Light level.
	 */
	protected void CustomRenderLabel(T entity,
			 Text text,
			 MatrixStack matrices,
			 VertexConsumerProvider vertexConsumers,
			 int light) {
		AobaClient aoba = Aoba.getInstance();
		
		double d = dispatcher.getSquaredDistanceToCamera((Entity)entity);
        if (d > 4096.0) {
            return;
        }
        boolean bl = !((Entity)entity).isSneaky();
        float f = ((Entity)entity).getNameLabelHeight();
        int i = "deadmau5".equals(text.getString()) ? -10 : 0;
        matrices.push();
        matrices.translate(0.0f, f, 0.0f);
        matrices.multiply(dispatcher.getRotation());
        matrices.scale(-0.025f, -0.025f, 0.025f);
        if(aoba.moduleManager.nametags.getState()) {
        	float scale = 1.0f;
        	Nametags nameTagsModule = (Nametags) aoba.moduleManager.nametags;
// TODO Player Entities are not PlayerEntity.
//        	if(nameTagsModule.getPlayersOnly()) {
//        		if(entity instanceof PlayerEntity) {
//        			scale = (float)nameTagsModule.getNametagScale();
//        		}
//        	}else {
        		scale = (float)nameTagsModule.getNametagScale();
//}
        	
        	matrices.scale(scale, scale, scale);
        }
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        int j = (int)(g * 255.0f) << 24;
        TextRenderer textRenderer = this.getTextRenderer();
        float h = -textRenderer.getWidth(text) / 2;
        textRenderer.draw(text, h, (float)i, 0x20FFFFFF, false, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, light);
        if (bl) {
            textRenderer.draw(text, h, (float)i, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        }
        matrices.pop();
	}
}
