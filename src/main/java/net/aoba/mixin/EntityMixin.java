package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.aoba.Aoba;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(Entity.class)
public class EntityMixin{
	
	@Inject(at = { @At("HEAD") }, method = "isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z", cancellable = true)
	private void onIsInvisibleCheck(PlayerEntity message, CallbackInfoReturnable<Boolean> cir) {
		if(Aoba.getInstance().moduleManager.antiinvis.getState()) {
			cir.setReturnValue(false);
		}
	}
}
