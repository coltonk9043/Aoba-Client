/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.interfaces.IMultiPlayerGameMode;
import net.aoba.module.modules.misc.RandomPlace;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin implements IMultiPlayerGameMode {
	@Unique
	private static final Random random = new Random();

	@Shadow
	protected abstract void ensureHasSentCarriedItem();

	@Override
	public void aoba$syncSelected() {
		ensureHasSentCarriedItem();
	}

	@Inject(method = "useItemOn", at = @At("RETURN"))
	private void beforeBlockPlace(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
		if (Aoba.getInstance() != null && Aoba.getInstance().moduleManager != null) {
			RandomPlace module = Aoba.getInstance().moduleManager.randomplace;
			if (module.state.getValue()) {
				int currentSlot = player.getInventory().getSelectedSlot();
				long nr_of_as = module.allowed_slots.getValue().stream()
						.filter(b -> b)
						.count();
				if (module.allowed_slots.getValueAt(currentSlot) && (player.getMainHandItem().getItem() instanceof BlockItem || !module.must_hold_block.getValue()) && nr_of_as > 0) {
					int newSlot;
					do {
						newSlot = random.nextInt(9);
					} while (!module.allowed_slots.getValueAt(newSlot) || !(player.getInventory().getItem(newSlot).getItem() instanceof BlockItem));
					player.getInventory().setSelectedSlot(newSlot);
				}
			}
		}
	}
}
