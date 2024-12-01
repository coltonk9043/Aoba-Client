package net.aoba.module.modules.misc;

import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.utils.discord.RPCManager;

public class DiscordRPCModule extends Module {

    public DiscordRPCModule() {
        super("DiscordRPC");

        this.setCategory(Category.of("Misc"));
        this.setDescription("Toggles Discord RPC On and Off");
    }

    @Override
    public void onDisable() {
        RPCManager rpcManager = new RPCManager();
        rpcManager.startRpc();
    }

    @Override
    public void onEnable() {
        RPCManager rpcManager = new RPCManager();
        rpcManager.StopRPC();
    }

    @Override
    public void onToggle() { // OnEnable And Disable Handle
    }
}
