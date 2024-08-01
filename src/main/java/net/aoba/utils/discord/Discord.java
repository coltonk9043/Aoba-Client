package net.aoba.utils.discord;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface Discord extends Library {
    Discord INSTANCE = Native.load("discord-rpc", Discord.class);

    void Discord_RunCallbacks();

    void Discord_Initialize(final String p0, final DiscordEventHandlers p1, final boolean p2, final String p3);
    void Discord_Shutdown();
    void Discord_UpdatePresence(final DiscordRPC p0);
}