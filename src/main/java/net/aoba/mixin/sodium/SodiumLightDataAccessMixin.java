package net.aoba.mixin.sodium;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.render.Fullbright;
import net.aoba.module.modules.render.XRay;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = LightDataAccess.class, remap = false)
public abstract class SodiumLightDataAccessMixin {
	@Shadow
	protected BlockAndTintGetter level;

	@Shadow
	@Final
	private BlockPos.MutableBlockPos pos;

	@Unique
	private XRay xray;

	@Unique
	private Fullbright fb;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(CallbackInfo info) {
		AobaClient aoba = Aoba.getInstance();
		if (aoba == null) return;
		xray = aoba.moduleManager.xray;
		fb = aoba.moduleManager.fullbright;
	}

	@ModifyArg(method = "compute", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/model/light/data/LightDataAccess;packBL(I)I"))
	private int compute_modifyBL(int blockLight) {
		if (xray != null && xray.state.getValue()) {
			BlockState state = level.getBlockState(pos);
			if (xray.isXRayBlock(state.getBlock())) {
				return 15;
			}
		}
		return blockLight;
	}
}
