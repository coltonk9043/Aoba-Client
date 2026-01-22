package net.aoba.mixin;

import net.aoba.Aoba;
import net.aoba.module.modules.misc.RandomPlace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ClientPlayerInteractionManager.class)
public class PlayerInteractBlockMixin {
    private static final Random random = new Random();

    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void beforeBlockPlace(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (Aoba.getInstance() != null && Aoba.getInstance().moduleManager != null) {
            RandomPlace module = Aoba.getInstance().moduleManager.randomplace;
            if (module.state.getValue()) {
                int currentSlot = player.getInventory().getSelectedSlot();
                long nr_of_as = module.allowed_slots.getValue().stream()
                        .filter(b -> b)
                        .count();
                if (module.allowed_slots.getValueAt(currentSlot) && (player.getMainHandStack().getItem() instanceof BlockItem || !module.must_hold_block.getValue()) && nr_of_as > 0) {
                    int newSlot;
                    do {
                        newSlot = random.nextInt(9);
                    } while (!module.allowed_slots.getValueAt(newSlot) || !(player.getInventory().getStack(newSlot).getItem() instanceof BlockItem));
                    player.getInventory().setSelectedSlot(newSlot);
                }
            }
        }
    }
}
