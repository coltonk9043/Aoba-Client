/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.huds;

import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.ResizeMode;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

public class PingHud extends HudWindow {
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	String pingText = null;

	public PingHud(int x, int y) {
		super("PingHud", x, y, 50, 24);
		minWidth = 50f;
		minHeight = 20f;
		maxHeight = 20f;
		resizeMode = ResizeMode.None;
	}

	@Override
	public void update() {
		ClientPlayNetworkHandler networkHandler = MC.getNetworkHandler();
		if (networkHandler != null && MC.player != null) {
			PlayerListEntry entry = networkHandler.getPlayerListEntry(MC.player.getUuid());
			if (entry != null) {
				int ping = entry.getLatency();
				pingText = "Ping: " + ping + " ms";
			} else {
				pingText = "Ping: ?";
			}
		} else
			pingText = null;
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		if (pingText != null && isVisible()) {
			Rectangle pos = position.getValue();
			if (pos.isDrawable()) {
				Render2D.drawString(drawContext, pingText, pos.getX(), pos.getY(),
						GuiManager.foregroundColor.getValue().getColorAsInt());
			}
		}
	}
}
