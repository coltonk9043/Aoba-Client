package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.movement.Freecam;
import net.aoba.module.modules.render.NoRender;
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
	private float lastTickProgress;

	@Inject(at = {
			@At("HEAD") }, method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", cancellable = true)
	private void onCameraUdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView,
			float tickDelta, CallbackInfo ci) {
		AobaClient aoba = Aoba.getInstance();
		if (aoba != null && aoba.moduleManager.freecam.state.getValue()) {
			ready = true;
			this.area = area;
			lastTickProgress = tickDelta;
			this.thirdPerson = thirdPerson;
			ci.cancel();
		}
	}

	@Inject(at = {
			@At("HEAD") }, method = "getSubmersionType()Lnet/minecraft/block/enums/CameraSubmersionType;", cancellable = true)
	private void onGetSubmersionType(CallbackInfoReturnable<CameraSubmersionType> cir) {
		AobaClient aoba = Aoba.getInstance();
		if (aoba == null)
			return;

		Freecam freecam = aoba.moduleManager.freecam;
		NoRender norender = aoba.moduleManager.norender;

		if (freecam.state.getValue() || (norender.state.getValue() && norender.getNoLiquidOverlay())) {
			cir.setReturnValue(CameraSubmersionType.NONE);
		}
	}
}
