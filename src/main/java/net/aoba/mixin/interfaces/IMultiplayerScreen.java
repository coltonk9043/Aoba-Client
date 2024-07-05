package net.aoba.mixin.interfaces;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;

@Mixin(MultiplayerScreen.class)
public interface IMultiplayerScreen {
    @Accessor("serverListWidget")
    MultiplayerServerListWidget getServerListWidget();

    @Accessor("serverListWidget")
    void setServerListWidget(MultiplayerServerListWidget val);
    
    @Invoker("connect")
    public void invokeConnect(ServerInfo entry);
}
