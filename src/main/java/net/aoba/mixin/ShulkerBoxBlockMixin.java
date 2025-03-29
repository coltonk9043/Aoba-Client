package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.ShulkerBoxBlock;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {
	// @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
	// private void onAppendTooltip(ItemStack stack, Item.TooltipContext context,
	// List<Text> tooltip, TooltipType options,
	// CallbackInfo info) {
	// Tooltips tooltips = Aoba.getInstance().moduleManager.tooltips;
//
	// if (tooltips.getStorage() && tooltips.state.getValue())
	// info.cancel();
	// }
}