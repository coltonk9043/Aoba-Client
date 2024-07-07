package net.aoba.mixin;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.misc.Timer;
import net.minecraft.client.render.RenderTickCounter.Dynamic;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Dynamic.class)
public class DynamicMixin {
    @Shadow
    private float lastFrameDuration;

    @Inject(at = {
            @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;prevTimeMillis:J", opcode = Opcodes.PUTFIELD, ordinal = 0)}, method = {
            "beginRenderTick(J)I"})
    public void onBeginRenderTick(long long_1, CallbackInfoReturnable<Integer> cir) {
        AobaClient aoba = Aoba.getInstance();
        if (aoba.moduleManager != null) {
            Timer timer = (Timer) Aoba.getInstance().moduleManager.timer;
            if (timer.getState()) {
                lastFrameDuration *= timer.getMultiplier();
            }
        }
    }
}
