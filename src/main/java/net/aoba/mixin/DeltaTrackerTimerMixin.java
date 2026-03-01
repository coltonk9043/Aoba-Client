package net.aoba.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.misc.Timer;
import net.minecraft.client.DeltaTracker;

@Mixin(DeltaTracker.Timer.class)
public class DeltaTrackerTimerMixin {
	@Shadow
	private float deltaTicks;

	@Inject(at = {
			@At(value = "FIELD", target = "Lnet/minecraft/client/DeltaTracker$Timer;lastMs:J", opcode = Opcodes.PUTFIELD, ordinal = 0) }, method = {
					"advanceGameTime(J)I" })
	public void onBeginRenderTick(long long_1, CallbackInfoReturnable<Integer> cir) {
		AobaClient aoba = Aoba.getInstance();
		if (aoba.moduleManager != null) {
			Timer timer = Aoba.getInstance().moduleManager.timer;
			if (timer.state.getValue()) {
				deltaTicks *= timer.getMultiplier();
			}
		}
	}
}
