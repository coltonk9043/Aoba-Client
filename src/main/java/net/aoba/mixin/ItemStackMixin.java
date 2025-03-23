package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.ItemUsedEvent;
import net.aoba.event.events.ItemUsedEvent.Post;
import net.aoba.event.events.ItemUsedEvent.Pre;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Inject(method = "finishUsing", at = @At("HEAD"))
	private void onFinishUsingPre(World world, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
		if (entity == AobaClient.MC.player) {
			Pre event = new ItemUsedEvent.Pre((ItemStack) (Object) this);
			Aoba.getInstance().eventManager.Fire(event);
		}
	}

	@Inject(method = "finishUsing", at = @At("TAIL"))
	private void onFinishUsingPost(World world, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
		if (entity == AobaClient.MC.player) {
			Post event = new ItemUsedEvent.Post((ItemStack) (Object) this);
			Aoba.getInstance().eventManager.Fire(event);
		}
	}
}
