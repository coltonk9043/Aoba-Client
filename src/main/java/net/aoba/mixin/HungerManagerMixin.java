package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.event.events.FoodLevelEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
	@Shadow
	private int foodLevel;
	
	@Shadow
	private float saturationLevel;
	
	@Shadow
	private float exhaustion;
	
	@Inject(at = { @At("HEAD") }, method = { "update(Lnet/minecraft/entity/player/PlayerEntity;)V" }, cancellable = true)
	private void onHungerUpdate(PlayerEntity player, CallbackInfo ci) {
		MinecraftClient mc = MinecraftClient.getInstance();
		
		if (player == mc.player) {
			Difficulty difficulty = player.getWorld().getDifficulty();
			if (this.exhaustion > 4.0f) {
				if (this.saturationLevel <= 0.0f && difficulty != Difficulty.PEACEFUL) {
					System.out.println("HUNGER EVENT ");
					// Fire Event
					int newFoodLevel = Math.max(this.foodLevel - 1, 0);
					FoodLevelEvent event = new FoodLevelEvent(newFoodLevel);
					Aoba.getInstance().eventManager.Fire(event);
				}
			}
		}
	}
	
	@Inject(at = { @At("HEAD") }, method = { "setFoodLevel(I)V" }, cancellable = true)
	private void onSetHunger(int foodLevel, CallbackInfo ci) {
		FoodLevelEvent event = new FoodLevelEvent(foodLevel);
		Aoba.getInstance().eventManager.Fire(event);
	}
}
