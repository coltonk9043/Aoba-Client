package net.aoba.mixin.interfaces;

import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JoinMultiplayerScreen.class)
public interface IJoinMultiplayerScreen {
    @Accessor("serverSelectionList")
    ServerSelectionList getServerListWidget();

    @Accessor("serverSelectionList")
    void setServerListWidget(ServerSelectionList val);

    @Invoker("join")
    void invokeConnect(ServerData entry);
}
