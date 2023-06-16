package net.aoba.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.aoba.Aoba;
import net.aoba.module.modules.misc.Timer;
import net.minecraft.client.render.RenderTickCounter;

@Mixin(RenderTickCounter.class)
public class RenderTickCounterMixin {
	@Shadow
	private float lastFrameDuration;
	

	@Inject(at = {@At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter;prevTimeMillis:J", 
			opcode = Opcodes.PUTFIELD, ordinal = 0) }, method = {"beginRenderTick(J)I" })
	public void onBeginRenderTick(long long_1, CallbackInfoReturnable<Integer> cir) {
		Timer timer = (Timer) Aoba.getInstance().moduleManager.timer;
		if(timer.getState()) {
			lastFrameDuration *= timer.getMultiplier();
		}
	}
}
