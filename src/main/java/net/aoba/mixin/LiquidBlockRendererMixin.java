package net.aoba.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.render.XRay;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {
    @Inject(method = "tesselate", at = @At("HEAD"), cancellable = true)
    private void onRender(BlockAndTintGetter world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        AobaClient aoba = Aoba.getInstance();
        XRay xray = aoba.moduleManager.xray;
        if (xray.state.getValue()) {
            if (!xray.isXRayBlock(blockState.getBlock()) && !xray.fluids.getValue()) {
                ci.cancel();
            }
        }
    }
}
