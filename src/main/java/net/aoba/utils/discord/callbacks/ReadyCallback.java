/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.discord.callbacks;

import com.sun.jna.Callback;
import net.aoba.utils.discord.DiscordUser;

public interface ReadyCallback extends Callback {
    void apply(DiscordUser p0);
}
