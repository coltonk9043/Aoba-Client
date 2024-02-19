package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.misc.FastBreak;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.world.World;

@Mixin (PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity{

	@Shadow 
	private PlayerInventory inventory;
	
	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"), cancellable = true)
	public void onGetBlockBreakingSpeed(BlockState blockState, CallbackInfoReturnable<Float> ci) {
		AobaClient aoba = Aoba.getInstance();
		FastBreak fastBreak = (FastBreak)aoba.moduleManager.fastbreak;
		if(fastBreak.getState()) {
			float speed = inventory.getBlockBreakingSpeed(blockState);
			speed *= fastBreak.multiplier.getValue();
			
			if(!fastBreak.ignoreWater.getValue()) {
				if(isSubmergedIn(FluidTags.WATER) || isSubmergedIn(FluidTags.LAVA) ||!isOnGround()) {
					speed /= 5.0F;
				}
			}
			
			ci.setReturnValue(speed);
		}
	}
}
