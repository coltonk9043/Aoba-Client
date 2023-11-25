package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

@Mixin(MinecraftClient.class)
public interface IMinecraftClient {
    @Accessor("itemUseCooldown")
    int getItemUseCooldown();
    
    @Accessor("itemUseCooldown")
    void setItemUseCooldown(int value);
    
    @Invoker("doItemUse")
    void useItem();
    
    @Mutable
    @Accessor("session")
    void setSession(Session session);
}
