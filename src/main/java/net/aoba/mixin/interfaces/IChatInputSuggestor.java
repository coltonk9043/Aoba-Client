package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.screen.ChatInputSuggestor;

@Mixin(ChatInputSuggestor.class)
public interface IChatInputSuggestor {
    @Invoker("show")
    void show();
}
