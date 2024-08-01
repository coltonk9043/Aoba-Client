package net.aoba.utils.discord.callbacks;

import com.sun.jna.Callback;
import net.aoba.utils.discord.DiscordUser;

public interface JoinRequestCallback extends Callback {
    void apply(final DiscordUser p0);
}
