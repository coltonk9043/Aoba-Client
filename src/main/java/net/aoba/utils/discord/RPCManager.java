/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.discord;

import net.aoba.AobaClient;
import net.aoba.gui.screens.*;
import net.aoba.gui.screens.alts.AddAltScreen;
import net.aoba.gui.screens.alts.AltScreen;
import net.aoba.gui.screens.alts.EditAltScreen;
import net.aoba.gui.screens.proxy.AddProxyScreen;
import net.aoba.gui.screens.proxy.EditProxyScreen;
import net.aoba.gui.screens.proxy.ProxyScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import static net.aoba.AobaClient.MC;

public class RPCManager {
    public static boolean started;
    private static final Discord rpc = Discord.INSTANCE;
    public static DiscordRPC presence = new DiscordRPC();
    private static Thread thread;

    public void startRpc() {
        if (!started) {
            started = true;
            DiscordEventHandlers handlers = new DiscordEventHandlers();
            rpc.Discord_Initialize("1268367396134191136", handlers, true, "");
            presence.startTimestamp = (System.currentTimeMillis() / 1000L);
            presence.largeImageText = "v" + AobaClient.AOBA_VERSION;
            rpc.Discord_UpdatePresence(presence);

            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    rpc.Discord_RunCallbacks();

                    presence.details = getDetails();

                    presence.state = "v" + AobaClient.AOBA_VERSION + " | MC 1.21";

                    presence.smallImageText = "logged as - " + MC.getSession().getUsername();
                    presence.smallImageKey = "https://minotar.net/helm/" + MC.getSession().getUsername() + "/100.png";


                    presence.button_label_1 = "Download";
                    presence.button_url_1 = "https://github.com/coltonk9043/Aoba-MC-Hacked-Client";

                    presence.largeImageKey = "https://i.imgur.com/D64DjG2.png";

                    rpc.Discord_UpdatePresence(presence);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ignored) {
                    }
                }
            }, "TH-RPC-Handler");
            thread.start();
        }
    }

    /*
     Stops the Discord Rich Presence (RPC).
     */
    public void stopRpc() {
        if (started) {
            started = false;

            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }

            rpc.Discord_Shutdown();

            thread = null;
        }
    }

    private String getDetails() {
        String result = "";

        if (MC.currentScreen instanceof TitleScreen || MC.currentScreen instanceof MainMenuScreen) {
            result = "In Main menu";
        } else if (MC.currentScreen instanceof MultiplayerScreen || MC.currentScreen instanceof AddServerScreen) {
            result = "Picking a server";
        } else if (MC.getCurrentServerEntry() != null) {
            result = "Playing on a server";
        } else if (MC.isInSingleplayer()) {
            result = "Playing singleplayer";
        } else if (MC.currentScreen instanceof OptionsScreen) {
            result = "Editing options";
        } else if (MC.currentScreen instanceof SelectWorldScreen) {
            result = "Selecting a world";
        } else if (MC.currentScreen instanceof AobaCreditsScreen) {
            result = "Watching Aoba credits";
        } else if (MC.currentScreen instanceof CreditsScreen) {
            result = "Watching credits";
        } else if (MC.currentScreen instanceof CreateWorldScreen) {
            result = "Creating a world";
        } else if (MC.currentScreen instanceof EditWorldScreen) {
            result = "Editing a world";
        } else if (MC.currentScreen instanceof ProxyScreen) {
            result = "Choosing a proxy";
        } else if (MC.currentScreen instanceof AltScreen) {
            result = "Choosing an alt";
        } else if (MC.currentScreen instanceof AddProxyScreen) {
            result = "Creating a proxy";
        } else if (MC.currentScreen instanceof EditProxyScreen) {
            result = "Editing a proxy";
        } else if (MC.currentScreen instanceof AddAltScreen) {
            result = "Creating an alt";
        } else if (MC.currentScreen instanceof EditAltScreen) {
            result = "Editing an alt";
        }
        return result;
    }
}
