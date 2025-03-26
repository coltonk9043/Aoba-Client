package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.module.modules.combat.AntiKnockback;
import net.aoba.module.modules.render.NoRender;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
	@Inject(at = { @At("HEAD") }, method = "setHealth(F)V")
	public void onSetHealth(float health, CallbackInfo ci) {
    }

	@Inject(at = { @At("HEAD") }, method = "tickNewAi()V", cancellable = true)
	public void onTickNewAi(CallbackInfo ci) {

	}

	@Inject(at = {
			@At("HEAD") }, method = "damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z", cancellable = true)
	public void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
    }

	@Inject(method = "spawnItemParticles", at = @At("HEAD"), cancellable = true)
	private void spawnItemParticles(ItemStack stack, int count, CallbackInfo info) {
		NoRender norender = Aoba.getInstance().moduleManager.norender;
		if (norender.state.getValue() && norender.getNoEatParticles()
				&& stack.getComponents().contains(DataComponentTypes.FOOD))
			info.cancel();
	}

	@Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
	private void onPushAwayFrom(CallbackInfo info) {
		AntiKnockback antiKnockback = Aoba.getInstance().moduleManager.antiknockback;
		if (antiKnockback.state.getValue() && antiKnockback.getNoPushEntities())
			info.cancel();
	}
}
