package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;

@Mixin(Camera.class)
public class CameraMixin {
	@Shadow
	private boolean ready;

	@Shadow
	private BlockView area;

	@Shadow
	private boolean thirdPerson;

	@Shadow
	private float lastTickDelta;

	@Inject(at = {
			@At("HEAD") }, method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", cancellable = true)
	private void onCameraUdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView,
			float tickDelta, CallbackInfo ci) {
		if (Aoba.getInstance().moduleManager.freecam.state.getValue()) {
			this.ready = true;
			this.area = area;
			this.lastTickDelta = tickDelta;
			this.thirdPerson = thirdPerson;
			ci.cancel();
		}
	}

	@Inject(at = {
			@At("HEAD") }, method = "getSubmersionType()Lnet/minecraft/block/enums/CameraSubmersionType;", cancellable = true)
	private void onGetSubmersionType(CallbackInfoReturnable<CameraSubmersionType> cir) {
		if (Aoba.getInstance().moduleManager.freecam.state.getValue()) {
			cir.setReturnValue(CameraSubmersionType.NONE);
		}
	}
}
