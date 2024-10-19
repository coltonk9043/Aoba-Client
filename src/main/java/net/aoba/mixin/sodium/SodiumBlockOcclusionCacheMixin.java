package net.aoba.mixin.sodium;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.render.XRay;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockOcclusionCache.class, remap = false)
public abstract class SodiumBlockOcclusionCacheMixin {
    @Unique
    private XRay xray;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        AobaClient aoba = Aoba.getInstance();
        xray = (XRay) aoba.moduleManager.xray;
    }

    @ModifyReturnValue(method = "shouldDrawSide", at = @At("RETURN"))
    private boolean shouldDrawSide(boolean original, BlockState state, BlockView view, BlockPos pos, Direction facing) {
        if (xray.getState()) {
            return !xray.isXRayBlock(state.getBlock());
        }

        return original;
    }
}